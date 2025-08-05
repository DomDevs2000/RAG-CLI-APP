#!/usr/bin/env python3

import subprocess
import sys
import os
from pathlib import Path

class Colors:
    GREEN = '\033[0;32m'
    BLUE = '\033[0;34m'
    RED = '\033[0;31m'
    NC = '\033[0m'

def print_status(message):
    print(f"{Colors.BLUE}[INFO]{Colors.NC} {message}")

def print_success(message):
    print(f"{Colors.GREEN}[SUCCESS]{Colors.NC} {message}")

def print_error(message):
    print(f"{Colors.RED}[ERROR]{Colors.NC} {message}")

def run_command(command, cwd=None):
    try:
        result = subprocess.run(
            command, 
            shell=True, 
            cwd=cwd,
            text=True
        )
        return result.returncode == 0
    except Exception as e:
        print_error(f"Error running command: {command} - {e}")
        return False

def main():
    script_dir = Path(__file__).parent.absolute()
    os.chdir(script_dir)

    print_status("Quick build starting...")

    print_status("Skipping Ollama Docker setup - using local Ollama installation")
    print_status("Building RAG application...")
    os.chdir("RAG")
    if not run_command("./mvnw clean package -DskipTests"):
        print_error("Failed to build RAG application")
        return 1
    print_success("RAG built successfully")

    os.chdir("..")

    print_status("Building CLI application...")
    os.chdir("CLI")
    if not run_command("./mvnw clean package -DskipTests"):
        print_error("Failed to build CLI application")
        return 1
    print_success("CLI built successfully")

    os.chdir("..")

    print_success("Quick build completed!")
    print("")
    print("Run CLI: java -jar CLI/target/CLI-0.0.1-SNAPSHOT.jar")
    print("Run RAG: java -jar RAG/target/RAG-0.0.1-SNAPSHOT.jar")
    print("")
    print("Note: Ensure Ollama is running locally at: http://localhost:11434")

    return 0

if __name__ == "__main__":
    sys.exit(main())
