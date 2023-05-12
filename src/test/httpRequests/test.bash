#!/bin/bash

# Keycloak Authorization Code Flow with Proof Key for Code Exchange (PKCE)
#
# Dependencies:
#
#   'brew install jq pup'
#
#   https://stedolan.github.io/jq/
#   https://github.com/ericchiang/pup


### ----------------------------

usage()
{
  printf 'Usage  : %s -a %s -r %s -c %s -l %s -u %s\n' "${0##*/}" \
    "<AUTHORITY>" "<REALM>" "<CLIENT_ID>" "<REDIRECT_URL>" "<USERNAME>"

  printf 'Example: %s -a "%s" -r "%s" -c "%s" -l "%s" -u "%s"\n' "${0##*/}" \
    "https://keycloak.example.com/auth" \
    "myrealm" \
    "myclient" \
    "https://myapp.example.com/" \
    "myusername"

  printf '\nAccepts password from stdin, env AUTHORIZATION_CODE_LOGIN_PASSWORD, or prompt.\n'
  exit 2
}

while getopts 'a:r:c:l:u:?h' c
do
  case $c in
    a) authority=$OPTARG ;;
    r) realm=$OPTARG ;;
    c) clientId=$OPTARG ;;
    l) redirectUrl=$OPTARG ;;
    u) username=$OPTARG ;;
    h|?) usage ;;
  esac
done

[[ -z $authority || -z $realm || -z $clientId || -z $redirectUrl || -z $username ]] && usage

password="$AUTHORIZATION_CODE_LOGIN_PASSWORD"
[[ -z $password ]] && read -rp "password: " -s password


### ----------------------------


base64url() { tr -d '[:space:]' | tr -- '+/' '-_' | tr -d = ; }

sha256sum() { printf "%s" "$1" | openssl dgst -binary -sha256 | openssl base64 -e | base64url ; }

codeVerifier=$(openssl rand -base64 96 | base64url)
echo "codeVerifier=$codeVerifier"
echo  "sum="+$(sha256sum "$codeVerifier")


cookieJar=$(mktemp "/tmp/cookie.jar.XXXX")
trap 'rm "$cookieJar"' EXIT

loginForm=$(curl -v --insecure -sSL --get --cookie "$cookieJar" --cookie-jar "$cookieJar"  \
  --data-urlencode "client_id=${clientId}" \
  --data-urlencode "redirect_uri=$redirectUrl" \
  --data-urlencode "scope=openid" \
  --data-urlencode "response_type=code" \
  --data-urlencode "code_challenge=$(sha256sum "$codeVerifier")" \
  --data-urlencode "code_challenge_method=S256" \
  "$authority/realms/$realm/protocol/openid-connect/auth" \
  | ../../../../pup.exe 'form attr{action}')


cat "$cookieJar"
loginForm=${loginForm//\&amp;/\&}
printf "loginForm=%s" "$loginForm"

echo;
echo;


#code=$(curl -k -sS --cookie "$cookieJar" --cookie-jar "$cookieJar" \
#  --data-urlencode "username=$username" \
#  --data-urlencode "password=$password" \
#   --write-out "%{redirect_url}" \
#     "$loginForm" )
 # )

 code=$(curl -v -i  -k -sS --cookie "$cookieJar" --cookie-jar "$cookieJar"   \
    -d "username=$username" \
    -d "password=$password" \
    -d "credentialId=" \
    "$loginForm" \
       )
 echo;echo;
printf "code=%s" "$code"


exit 0

accessToken=$(curl  --insecure  -sS --cookie "$cookieJar" --cookie-jar "$cookieJar" \
  --data-urlencode "client_id=$clientId" \
  --data-urlencode "redirect_uri=$redirectUrl" \
  --data-urlencode "code=$code" \
  --data-urlencode "code_verifier=$codeVerifier" \
  --data-urlencode "grant_type=authorization_code" \
  "$authority/realms/$realm/protocol/openid-connect/token" \
  | ../../../../jq.exe -r ".access_token")

printf "%s" "$accessToken"
exit 0