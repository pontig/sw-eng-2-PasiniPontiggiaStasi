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
Fork the repository and then create a workflow action with the following code:
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
Complete the section team_name

Till the application is hosted locally set << NGROK URL >>

### REPOSITORY ORGANIZATION:
Your repository should look something like this:
``` java
BATTLE NAME
|-- .github/workflows
|-- CKBProblem
|   |-- Problem.pdf
|-- Language-Project
|   |-- src
|   |   |-- main
|   |   |   |-- language
|   |   |   |   |-- main.language
|   |   |-- main
|   |-- buildScript.extension
|-- README.md
```
You are allowed to modify only the README.md file,
moreover you can add other .language file in the folder _'language'_.

Whoever will not follow the rules will receive a grade equals to 0

### Good Luck 