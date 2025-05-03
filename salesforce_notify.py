import os
import requests
import json
import sys

# Load credentials from environment (will be passed from Jenkins)
SF_USERNAME = os.environ['SF_USERNAME']
SF_PASSWORD = os.environ['SF_PASSWORD']
SF_SECURITY_TOKEN = os.environ['SF_SECURITY_TOKEN']
SF_CONSUMER_KEY = os.environ['SF_CONSUMER_KEY']
SF_CONSUMER_SECRET = os.environ['SF_CONSUMER_SECRET']
SF_ENDPOINT = 'https://mlcg-dev-ed.my.salesforce.com/services/apexrest/jenkins/tests/webook'
SF_LOGIN_URL = 'https://login.salesforce.com/services/oauth2/token'

def notify_salesforce(promotion_id, build_number, build_status):
    try:
        # 1. Get OAuth token
        auth_payload = {
            'grant_type': 'password',
            'client_id': SF_CONSUMER_KEY,
            'client_secret': SF_CONSUMER_SECRET,
            'username': SF_USERNAME,
            'password': f"{SF_PASSWORD}{SF_SECURITY_TOKEN}"
        }
        
        auth_response = requests.post(
            SF_LOGIN_URL,
            data=auth_payload,
            headers={'Content-Type': 'application/x-www-form-urlencoded'}
        )
        auth_response.raise_for_status()
        access_token = auth_response.json()['access_token']

        # 2. Send build status
        payload = {
            "promotionTestId": promotion_id,
            "BuildNumber": build_number,
            "Status": build_status
        }
        
        response = requests.post(
            SF_ENDPOINT,
            json=payload,
            headers={
                'Authorization': f'Bearer {access_token}',
                'Content-Type': 'application/json'
            }
        )
        response.raise_for_status()
        print("Successfully notified Salesforce")
        
    except Exception as e:
        print(f"Error notifying Salesforce: {str(e)}")
        sys.exit(1)

if __name__ == "__main__":
    # Read parameters from command line
    promotion_id = sys.argv[1]
    build_number = sys.argv[2]
    build_status = sys.argv[3]
    
    notify_salesforce(promotion_id, build_number, build_status)
