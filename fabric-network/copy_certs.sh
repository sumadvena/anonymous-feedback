#!/usr/bin/env sh

ASSETS_PATH=/home/matt/AndroidStudioProjects/pandv/app/src/main/assets
CERT_PATH=/home/matt/pwr/7sem/inz/tnetnano/crypto-config/peerOrganizations
rm "$ASSETS_PATH"/*

cp "$CERT_PATH"/students.uni.edu/peers/peer0.students.uni.edu/tls/ca.crt "$ASSETS_PATH"/stu_ca.crt
# cp "$CERT_PATH"/professors.uni.edu/peers/peer0.professors.uni.edu/tls/ca.crt "$ASSETS_PATH"/prof_ca.crt

cp "$CERT_PATH"/students.uni.edu/users/User1@students.uni.edu/msp/keystore/priv_sk "$ASSETS_PATH"/stu_priv_sk
# cp "$CERT_PATH"/professors.uni.edu/users/User1@professors.uni.edu/msp/keystore/priv_sk "$ASSETS_PATH"/prof_priv_sk

cp "$CERT_PATH"/students.uni.edu/users/User1@students.uni.edu/msp/signcerts/User1@students.uni.edu-cert.pem "$ASSETS_PATH"/
# cp "$CERT_PATH"/professors.uni.edu/users/User1@professors.uni.edu/msp/signcerts/User1@professors.uni.edu-cert.pem "$ASSETS_PATH"/
