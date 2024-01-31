# CKB RULES

### RULES:
 1. You Do Not Talk About CKB Platform. 
 2. You Do **NOT** Talk About CKB Platform.
 3. If Someone Says "STOP" Or Goes Limp, Taps Out, The Fight Is Over. 
 4. Only Subscribed Teams to a fight. 
 5. Multiple Fight At A Time. 
 6. No ChatGPT, No StackOverflow. 
 7. Fights Will Go On As Long As they have to. 
 8. If This Is Your First Time At CKB Platform, You Have To Fight.

### WORKFLOW ACTION TEMPLATE:
``` java
name: Pull Request Notification
on:
  push:
    branches:
      - main 
jobs:
  notify:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout repository
        uses: actions/checkout@v2
      - name: Notify Endpoint
        run: |
            team_name=" "
            payload="{\"pusher\": \"${{ github.actor }}\", \"team\": \"$team_name\", \"repository\": \"${{ github.repository }}\" }"
            curl -X POST -H "Content-Type: application/json" -d "$payload" <<NGROK URL>>/ckb_platform/battle/pulls
```

Complete the section team_name, tournament_name and << NGROK URL >>

### Good Luck