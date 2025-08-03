#!/bin/bash

echo "Building optimized native CLI application..."

# Set native image environment variables for better performance
export NATIVE_IMAGE_CONFIG_FILE="src/main/resources/META-INF/native-image"

echo "Step 1: Clean and compile..."
./mvnw clean compile

echo "Step 2: Process AOT..."
./mvnw spring-boot:process-aot

echo "Step 3: Build optimized native image..."
# Use more memory for faster builds and enable parallel compilation
export MAVEN_OPTS="-Xmx4g"
./mvnw -Pnative native:compile -Dgraalvm.version=21.0.1 -Djava.awt.headless=true

if [ -f "target/rag-cli" ]; then
    echo "✅ Native image built successfully!"
    echo "📁 Binary location: target/rag-cli"
    echo "📊 Binary size:"
    ls -lh target/rag-cli
    echo ""
    echo "🚀 To run: ./target/rag-cli"
else
    echo "❌ Native image build failed!"
    exit 1
fi

