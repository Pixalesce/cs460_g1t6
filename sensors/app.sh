#!/bin/bash

echo "Creating Python virtual environment..."
python3 -m venv venv
echo "Starting Python virtual environment..."
source venv/bin/activate

echo "Updating system directory files..."
sudo apt-get update
echo "Installing necessary system packages..."
sudo apt install libgirepository1.0-dev libdbus-1-dev libcairo2-dev gobject-introspection libgl1 python3-dbus bluez

echo "Installing Python dependencies..."
pip install --upgrade pip
pip install -r requirements.txt

echo "Starting Python classes..."
nohup python ble-proximity/pairing_workflow.py &
nohup python ble-proximity/discover.py &
nohup python Weight/Weight.py &
nohup python Motion\ Detector/motion_detector.py &

echo "Setup complete."

