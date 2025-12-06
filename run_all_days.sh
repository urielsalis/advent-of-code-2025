#!/bin/bash

# Find all Day*.kt files and sort them
days=$(ls src/Day*.kt | sort -V | sed 's/src\///' | sed 's/.kt//')

for day in $days; do
    echo "========================================"
    echo "Running $day"
    echo "========================================"

    # Run using Gradle
    ./gradlew run --quiet --console=plain -PmainClass=${day}Kt
    echo ""
done

echo "========================================"
echo "All days completed!"
echo "========================================"