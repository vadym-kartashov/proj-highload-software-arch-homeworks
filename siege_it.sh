#!/bin/bash

# Parse command-line arguments
concurrent=$1
test_duration=$2
delay_between_requests=$3

# Check if all three arguments are provided
if [[ -z "$concurrent" || -z "$test_duration" || -z "$delay_between_requests" ]]; then
  echo "Usage: $0 <concurrent_users> <test_duration> <delay_between_requests>"
  exit 1
fi

# Prepare set of randomized requests for Siege
./prepare_for_siege.sh

# Run the Siege test with provided attributes
siege  --content-type "application/json" --log="load_test_results_random.log"  -c "$concurrent" -t "$test_duration" -d "$delay_between_requests" -f "random.siege"