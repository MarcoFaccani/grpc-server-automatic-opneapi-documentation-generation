#!/bin/bash

# Controlla che siano stati forniti due argomenti
if [ "$#" -ne 1 ]; then
    echo "Error add_examples_to_openapi.sh: missing required argument"
    exit 1
fi

# Definisci i percorsi dei file
OPENAPI_FILE="$1"

# Define the base directory for examples
BASE_DIR="service/target/test-json"

read_json_file_content() {
  local file="$1"
  if [ -f "$file" ]; then
    # Utilizza l'operatore di redirect (<) per leggere direttamente il contenuto del file
    content=$(<"$file")
  else
    content="{}"
  fi
  echo "$content"
}

# Function to add examples to the OpenAPI file
add_examples_to_given_endpoint() {
  local endpoint=$1
  local operation=$2
  local master_dir=$3 # folder that contains all the request/responses samples for a given ENDPOINT

  for scenario_dir in "$master_dir"/*/; do
    scenario=$(basename "$scenario_dir")
    request_file="$scenario_dir/request.json"

    if [ -f "$request_file" ] && [ -s "$request_file" ]; then
      request_content=$(read_json_file_content "$request_file")

      # Add request example
      jq --arg endpoint "$endpoint" \
         --arg operation "$operation" \
         --arg scenario "$scenario" \
         --argjson example "$request_content" \
         '.paths[$endpoint][$operation].requestBody.content["application/json"].examples[$scenario] = {value: $example}' \
         "$OPENAPI_FILE" > tmp.$$.json && mv tmp.$$.json "$OPENAPI_FILE"
    fi

    response_file="$scenario_dir/response.json"
    if [ -f "$response_file" ] && [ -s "$response_file" ]; then
      response_content=$(read_json_file_content "$response_file")

      # Add response example
      jq --arg endpoint "$endpoint" \
         --arg operation "$operation" \
         --arg scenario "$scenario" \
         --argjson example "$response_content" \
         '.paths[$endpoint][$operation].responses["200"].content["application/json"].examples[$scenario] = {value: $example}' \
         "$OPENAPI_FILE" > tmp.$$.json && mv tmp.$$.json "$OPENAPI_FILE"
    fi
  done
}


# Leggi i dati dal file proto-info-extracted.txt
while IFS=',' read -r method url verb; do
  # Rimuovi spazi bianchi dal metodo e url
  method=$(echo "$method" | tr -d '[:space:]')
  url=$(echo "$url" | tr -d '[:space:]')

  # Debug: Print how the function is being invoked
  echo "Adding examples for endpoint: $url, method: $verb, base directory: $BASE_DIR/$method"

  # Aggiungi esempi per l'endpoint
  add_examples_to_given_endpoint "$url" "$verb" "$BASE_DIR/$method"
done < proto-info-extracted.txt

echo "Examples added successfully to $OPENAPI_FILE"
