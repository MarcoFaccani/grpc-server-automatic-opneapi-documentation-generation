#!/bin/bash

# Assicurati che gli script siano eseguibili
chmod +x scripts/convert_swagger2_to_openapi3.sh
chmod +x scripts/add_examples_to_openapi.sh

# Path del file generato da buf
GEN_PATH="api/src/main/gen/openapiv2/proto/v1"
OPENAPI_FILE_OUTPUT_PATH="api/src/main/gen/openapiv3"
PROTO_DIRECTORY="api/src/main/proto/v1"

# Find proto file
proto_path=$(find "$PROTO_DIRECTORY" -type f -name "*.proto" | head -n 1)
if [ -n "$proto_path" ]; then
  echo "Proto file found: $proto_path"
else
  echo "Error: no .proto found in $directory"
  exit 1
fi


# Execute kotlin script
kotlinc-jvm -script scripts/extract_endpoint_info_from_proto.kts $proto_path

# 1. Verifica se esiste un file con estensione .json
echo "Verifying if older swagger2 documentation exists..."
EXISTING_JSON=$(find "$GEN_PATH" -type f -name "*.json")
if [ -n "$EXISTING_JSON" ]; then
    echo "Found an already existing swagger at path $GEN_PATH. Proceeding with the deletion."
    rm "$EXISTING_JSON"
    if [ $? -eq 0 ]; then
        echo "Deletion completed successfully."
    else
        echo "Error during deletion: $?"
        exit 1
    fi
fi

# 2. Esegui buf generate
echo "Producing swagger2 documentation from proto file..."
(cd api/src/main && buf generate)
BUF_EXIT_CODE=$?
if [ $BUF_EXIT_CODE -ne 0 ]; then
    echo "buf generate failed with exit code $BUF_EXIT_CODE."
    exit $BUF_EXIT_CODE
fi
echo "Swagger2 documentation generation completed successfully."

# 3. Verifica se è stato generato un nuovo file .json
NEW_JSON=$(find "$GEN_PATH" -type f -name "*.json")
if [ -n "$NEW_JSON" ]; then
    echo "Successfully found new JSON file in: $NEW_JSON"

    # Esegui convert_swagger2_to_openapi3.sh e salva il risultato
    echo "Converting from Swagger 2 to OpenAPI 3..."
    ./scripts/convert_swagger2_to_openapi3.sh "$NEW_JSON"
    if [ $? -ne 0 ]; then
        echo "Failed to convert Swagger 2 to OpenAPI 3. Exiting."
        exit 1
    fi

    # Definisci il percorso del file OpenAPI generato
    OPENAPI_FILE="response.json"

    # Esegui lo script per aggiungere gli esempi al file OpenAPI
    echo "Adding examples to OpenAPI file..."
    ./scripts/add_examples_to_openapi.sh "$OPENAPI_FILE"
    if [ $? -ne 0 ]; then
        echo "Failed to add examples to OpenAPI file. Exiting."
        exit 1
    fi

    # Crea la directory OPENAPI_FILE_OUTPUT_PATH se non esiste
    mkdir -p "$OPENAPI_FILE_OUTPUT_PATH"

    # Sposta il file response.json in api/src/main/gen/openapiv3 e rinominalo in openapi.json
    echo "Moving response.json to $OPENAPI_FILE_OUTPUT_PATH/openapi.json"
    mv "response.json" "$OPENAPI_FILE_OUTPUT_PATH/openapi.json"
    if [ $? -ne 0 ]; then
        echo "Failed to move response.json to $OPENAPI_FILE_OUTPUT_PATH/openapi.json. Exiting."
        exit 1
    fi

else
    echo "No JSON file found in $GEN_PATH."
    exit 1
fi

echo "Master script executed successfully. OpenAPI File generated in $OPENAPI_FILE_OUTPUT_PATH/openapi.json"
