#!/usr/bin/env sh
#
# SPDX-License-Identifier: Apache-2.0
#

# look for binaries in local dev environment /build/bin directory and then in local samples /bin directory
export PATH="${PWD}"/../../fabric/build/bin:"${PWD}"/../bin:"$PATH"

v=1

./peer3admin.sh

peer lifecycle chaincode install basic.tar.gz >> ./logs/install\&approve\&commit_chaincode_peer_3.log 2>&1

# Set the CHAINCODE_ID from the created chaincode package
CHAINCODE_ID=$(peer lifecycle chaincode calculatepackageid basic.tar.gz)
export CHAINCODE_ID

# Approve the chaincode using Peer1Admin
peer lifecycle chaincode approveformyorg -o 127.0.0.1:6050 --channelID mychannel --name basic --version "$v" --package-id "${CHAINCODE_ID}" --sequence "$v" --tls --cafile "${PWD}"/crypto-config/ordererOrganizations/uni.edu/orderers/orderer.uni.edu/tls/ca.crt >> ./logs/install\&approve\&commit_chaincode_peer_3.log 2>&1

