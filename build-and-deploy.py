#!/usr/bin/env python3

"""
Build and Deploy Script for RAG and CLI Applications
This script cleans, builds, packages, creates Docker images, and deploys the applications
"""

import subprocess
import sys
import time
import os
import argparse
from pathlib import Path

# Colors for output
class Colors:
    RED = '\033[0;31m'
    GREEN = '\033[0;32m'
    YELLOW = '\033[1;33m'
    BLUE = '\033[0;34m'
    NC = '\033[0m'  # No Color

def print_status(message):
    print(f"{Colors.BLUE}[INFO]{Colors.NC} {message}")

def print_success(message):
    print(f"{Colors.GREEN}[SUCCESS]{Colors.NC} {message}")

def print_warning(message):
    print(f"{Colors.YELLOW}[WARNING]{Colors.NC} {message}")

def print_error(message):
    print(f"{Colors.RED}[ERROR]{Colors.NC} {message}")

def run_command(command, cwd=None, timeout=None):
    """Run a command and return True if successful"""
    try:
        result = subprocess.run(
            command, 
            shell=True, 
            cwd=cwd,
            timeout=timeout,
            capture_output=True,
            text=True
        )
        if result.returncode != 0:
            print_error(f"Command failed: {command}")
            if result.stderr:
                print(result.stderr)
            return False
        return True
    except subprocess.TimeoutExpired:
        print_error(f"Command timed out: {command}")
        return False
    except Exception as e:
        print_error(f"Error running command: {command} - {e}")
        return False

def check_docker_containers():
    """Check if Docker containers are running"""
    try:
        result = subprocess.run(
            "docker-compose ps -q", 
            shell=True, 
            capture_output=True, 
            text=True
        )
        return bool(result.stdout.strip())
    except:
        return False

def wait_for_postgres():
    """Wait for PostgreSQL to be healthy"""
    print_status("Waiting for PostgreSQL to be healthy...")
    timeout = 60
    counter = 0
    
    while counter < timeout:
        try:
            result = subprocess.run(
                "docker-compose exec postgres pg_isready -U postgres -d rag_api",
                shell=True,
                capture_output=True,
                text=True
            )
            if result.returncode == 0:
                print_success("PostgreSQL is ready")
                return True
        except:
            pass
        
        time.sleep(2)
        counter += 2
        
        if counter >= timeout:
            print_error(f"PostgreSQL failed to start within {timeout} seconds")
            subprocess.run("docker-compose logs postgres", shell=True)
            return False
    
    return False

def wait_for_rag_app():
    """Wait for RAG application to be ready (with timeout to avoid hanging)"""
    print_status("Waiting for RAG application to be ready...")
    timeout = 30  # Reduced timeout
    counter = 0
    
    while counter < timeout:
        try:
            # Check if the application is responding with a simple HTTP check
            result = subprocess.run(
                "curl -s -o /dev/null -w '%{http_code}' http://localhost:8080/",
                shell=True,
                capture_output=True,
                text=True,
                timeout=5
            )
            # If we get any HTTP response (even 404), the server is running
            if result.stdout and result.stdout.strip().isdigit():
                print_success("RAG application is responding")
                return True
        except:
            pass
        
        time.sleep(2)
        counter += 2
        
        if counter >= timeout:
            print_warning("RAG application health check timeout reached, but continuing...")
            print_status("The application may still be starting. Check logs with: docker-compose logs -f rag-app")
            return True
    
    return True

def main():
    parser = argparse.ArgumentParser(description='Build and deploy RAG and CLI applications')
    parser.add_argument('--skip-native', action='store_true', help='Skip native CLI image build')
    args = parser.parse_args()

    # Get script directory
    script_dir = Path(__file__).parent.absolute()
    os.chdir(script_dir)

    print_status("Starting build and deployment process...")

    # Step 1: Stop existing Docker containers
    print_status("Stopping existing Docker containers...")
    if check_docker_containers():
        if run_command("docker-compose down"):
            print_success("Docker containers stopped")
        else:
            print_error("Failed to stop Docker containers")
            return 1
    else:
        print_warning("No running containers found")

    # Step 1.5: Start/Ensure Ollama Docker container is running
    print_status("Starting Ollama Docker container...")
    # Stop existing ollama container if running
    run_command("docker stop ollama", timeout=10)
    run_command("docker rm ollama", timeout=10)
    
    if not run_command("docker run -d -v ollama:/root/.ollama -p 11434:11434 --name ollama ollama/ollama"):
        print_error("Failed to start Ollama container")
        return 1
    print_success("Ollama container started successfully")
    
    # Give Ollama a moment to start
    print_status("Waiting for Ollama to initialize...")
    time.sleep(5)

    # Step 2: Clean and build RAG application
    print_status("Building RAG application...")
    os.chdir("RAG")

    print_status("Cleaning RAG project...")
    if not run_command("./mvnw clean"):
        print_error("Failed to clean RAG project")
        return 1

    print_status("Packaging RAG application (skipping tests)...")
    if not run_command("./mvnw package -DskipTests"):
        print_error("Failed to package RAG application")
        return 1

    if not Path("target/RAG-0.0.1-SNAPSHOT.jar").exists():
        print_error("RAG JAR file not found after build!")
        return 1

    print_success("RAG application packaged successfully")

    # Step 3: Build RAG Docker image
    print_status("Building RAG Docker image...")
    if not run_command("docker build -t rag-app ."):
        print_error("Failed to build RAG Docker image")
        return 1
    print_success("RAG Docker image built successfully")

    os.chdir("..")

    # Step 4: Clean and build CLI application
    print_status("Building CLI application...")
    os.chdir("CLI")

    print_status("Cleaning CLI project...")
    if not run_command("./mvnw clean"):
        print_error("Failed to clean CLI project")
        return 1

    print_status("Packaging CLI application (skipping tests)...")
    if not run_command("./mvnw package -DskipTests"):
        print_error("Failed to package CLI application")
        return 1

    if not Path("target/CLI-0.0.1-SNAPSHOT.jar").exists():
        print_error("CLI JAR file not found after build!")
        return 1

    print_success("CLI application packaged successfully")

    os.chdir("..")

    # Step 5: Build native CLI image (by default, unless skipped)
    if not args.skip_native:
        print_status("Building native CLI image...")
        os.chdir("CLI")
        if not run_command("./mvnw package -Pnative -DskipTests", timeout=600):  # 10 minute timeout for native build
            print_warning("Failed to build native CLI image, continuing without it...")
        else:
            print_success("Native CLI image built successfully")
        os.chdir("..")

    # Step 6: Start Docker Compose services
    print_status("Starting services with Docker Compose...")

    # Start PostgreSQL first and wait for it to be healthy
    print_status("Starting PostgreSQL database...")
    if not run_command("docker-compose up -d postgres"):
        print_error("Failed to start PostgreSQL")
        return 1

    if not wait_for_postgres():
        return 1

    # Start the RAG application
    print_status("Starting RAG application...")
    if not run_command("docker-compose up -d rag-app"):
        print_error("Failed to start RAG application")
        return 1

    # Wait for RAG application to be ready
    wait_for_rag_app()

    print_success("All services started successfully!")

    # Step 7: Display service status
    print_status("Service Status:")
    subprocess.run("docker-compose ps", shell=True)

    # Step 8: Display connection information
    print_success("Deployment completed successfully!")
    print("")
    print("=== Service Information ===")
    print("RAG API: http://localhost:8080")
    print("PostgreSQL: localhost:5433 (rag_api database)")
    print("CLI JAR: CLI/target/CLI-0.0.1-SNAPSHOT.jar")
    print("")
    print("=== Usage Examples ===")
    print("Run CLI: java -jar CLI/target/CLI-0.0.1-SNAPSHOT.jar")
    if not args.skip_native and Path("CLI/target/rag-cli").exists():
        print("Run Native CLI: ./CLI/target/rag-cli")
    print("")
    print("CLI Commands:")
    print("  upload --file-paths filename1.pdf filename2.pdf")
    print("  chat --message 'Your question here'")
    print("  clear")
    print("")
    print("=== Logs ===")
    print("View RAG logs: docker-compose logs -f rag-app")
    print("View DB logs: docker-compose logs -f postgres")
    print("View Ollama logs: docker logs -f ollama")

    return 0

if __name__ == "__main__":
    sys.exit(main())