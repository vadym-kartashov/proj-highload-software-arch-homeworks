#!/bin/bash

# Function to generate random strings
randstring() {
  strings=("$@")
  echo ${strings[$RANDOM % ${#strings[@]}]}
}

# Function to generate a random integer in a given range
randint() {
  min=$1
  max=$2
  echo $((RANDOM % (max - min + 1) + min))
}

# Create a file to store the random requests
output_file="random.siege"
echo "" > "$output_file"

# Generate 100 random requests and append them to the output file
for i in {1..100}; do
  # Random values for GET /people/{id} endpoint
  get_id=$(randint 1 100000)
  get_request="http://localhost/people/$get_id"

  # Random values for POST /people endpoint with JSON payload
  post_id=$get_id
  post_name=$(randstring "John" "Alice" "Bob")
  post_age=$(randint 18 60)
  post_request="http://localhost/people POST {\"id\": \"$post_id\", \"name\": \"$post_name\", \"age\": $post_age}"

  # Append the generated requests to the output file
  echo -e "$post_request" >> "$output_file"
  echo -e "$get_request" >> "$output_file"

  # Add a blank line to separate requests
  #echo "" >> "$output_file"
done

echo "Random requests have been generated and saved in $output_file."