import os

def create_config():
    config_content = """
# Configuration MediCare+
DB_HOST = "localhost"
DB_NAME = "medicare_db"
DEBUG = True
VERSION = "1.0.0"
"""
    with open("config.py", "w") as f:
        f.write(config_content)
    print("Fichier de configuration généré avec succès.")

if __name__ == "__main__":
    create_config()