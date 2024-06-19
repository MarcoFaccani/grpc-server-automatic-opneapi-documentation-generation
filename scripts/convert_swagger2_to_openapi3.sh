#!/bin/bash

# Controlla che sia stato fornito un file JSON come argomento
if [ "$#" -ne 1 ]; then
    echo "convert_swagger2_to_openapi3.sh: argument missing. Please provide a path to a swagger2 JSON file"
    exit 1
fi

# Definisci il percorso del file JSON
JSON_FILE="$1"

# Verifica se il file JSON esiste
if [ ! -f "$JSON_FILE" ]; then
    echo "File not found: $JSON_FILE"
    exit 1
fi

# Effettua una chiamata curl alla REST API
RESPONSE=$(curl -s -o response.json -w "%{http_code}" -X POST https://converter.swagger.io/api/convert \
-H "Content-Type: application/json" \
-d @"$JSON_FILE")

# Estrai il codice di stato HTTP dalla variabile RESPONSE
HTTP_STATUS=$(echo "$RESPONSE" | tail -n1)

# Verifica il codice di stato della risposta
if [ "$HTTP_STATUS" -eq 200 ]; then
    echo "Conversion successful."
else
    echo "Conversion failed. HTTP Status: $HTTP_STATUS"
    cat response.json
    exit 1
fi
