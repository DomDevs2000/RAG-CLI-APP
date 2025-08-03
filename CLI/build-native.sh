#!/bin/bash

echo "Building native CLI application..."

echo "Step 1: Clean and compile..."
./mvnw clean compile

echo "Step 2: Process AOT..."
./mvnw spring-boot:process-aot

echo "Step 3: Build native image..."
./mvnw -Pnative native:compile

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

