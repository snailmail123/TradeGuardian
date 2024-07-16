const {
    loadFixture,
} = require("@nomicfoundation/hardhat-toolbox/network-helpers");

const TWO_HOURS_IN_SECS = 7200;
const ONE_WEI = ethers.parseEther("0.000000000000000001");
const BUYER_DEPOSIT_MULTIPLIER = 2n;
const PERCENTAGE_RETURNED_TO_BUYER = 25n;
const FEE_PERCENTAGE = 1n;
const EscrowStates = {
    AWAITING_DEPOSIT: 0,
    AWAITING_DELIVERY: 1,
    ITEM_SENT: 2,
    ITEM_INCORRECT: 3,
    ITEM_RECEIVED: 4,
    SETTLED: 5,
    CANCELED: 6
};

async function deployEscrow() {
    const itemPrice = ethers.parseEther("1");
    const [feeRecipient, seller, buyer, thirdParty] = await ethers.getSigners();

    const Escrow = await ethers.getContractFactory("Escrow");
    const escrow = await Escrow.deploy(feeRecipient.address, seller.address, buyer.address, itemPrice);

    return { escrow, feeRecipient, seller, buyer, thirdParty, itemPrice };
}

module.exports = {
    TWO_HOURS_IN_SECS,
    ONE_WEI,
    BUYER_DEPOSIT_MULTIPLIER,
    PERCENTAGE_RETURNED_TO_BUYER,
    FEE_PERCENTAGE,
    EscrowStates,
    loadFixture,
    deployEscrow
};
