import random
import string
import time
from concurrent.futures._base import LOGGER
from datetime import datetime

import psycopg2
from captcha.image import ImageCaptcha

db_params = {
    'host': 'postgres',
    'dbname': 'postgres',
    'user': 'myuser',
    'password': 'mypassword',
    'port': '5432'
}

while True:
    try: 
        conn = psycopg2.connect(**db_params)
        if conn:
            break
    except Exception as e:
        LOGGER.warning(f"++++ Retrying connection to the database because of the issue {str(e)}++++")
    time.sleep(3)
        
cursor = conn.cursor()

cursor.execute("""
    CREATE TABLE IF NOT EXISTS captchas (
        id SERIAL PRIMARY KEY,
        code CHAR(6),
        image BYTEA,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    )
""")

conn.commit()

def generate_captcha():
    random_string = ''.join(random.choices(string.ascii_uppercase, k=6))
    captcha: ImageCaptcha = ImageCaptcha(width=400,
                                         height=220,
                                         fonts=['/times.ttf',],
                                         font_sizes=(70, 90))
    image_data = captcha.generate(random_string)
    image_bytes = image_data.getvalue()
    return random_string, image_bytes

for _ in range(1000):
    code, captcha_image = generate_captcha()
    cursor.execute("INSERT INTO captchas (code, image, created_at) VALUES (%s, %s, %s)",
                   (code, psycopg2.Binary(captcha_image), datetime.now(),))
    
conn.commit()

cursor.close()
conn.close()
