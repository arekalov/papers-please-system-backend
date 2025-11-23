#!/bin/bash

# Activate virtual environment and run the Python script
cd "$(dirname "$0")"
source venv/bin/activate
python dump_data.py
deactivate

