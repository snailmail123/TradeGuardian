// /test/escrow/deployment.test.js

const { expect } = require("chai");
const { 
    loadFixture,
    deployEscrow, 
    TWO_HOURS_IN_SECS,
    EscrowStates 
} = require("./utils");

describe("Deployment", function () {
    it("Should deploy the Escrow contract correctly", async function () {
        const { escrow } = await loadFixture(deployEscrow);
        const address = await escrow.getAddress();
        expect(address).to.exist;
    });

    it("Should set the correct escrow state", async function () {
        const { escrow } = await loadFixture(deployEscrow);
        const currentState = await escrow.currentState();
        expect(currentState).to.equal(EscrowStates.AWAITING_DEPOSIT);
    });

    it("Should correctly set the feeRecipient", async function () {
        const { escrow, feeRecipient } = await loadFixture(deployEscrow);
        expect(await escrow.feeRecipient()).to.equal(feeRecipient.address);
    });

    it("Should correctly set the seller", async function () {
        const { escrow, seller } = await loadFixture(deployEscrow);
        expect(await escrow.seller()).to.equal(seller.address);
    });

    it("Should correctly set the buyer", async function () {
        const { escrow, buyer } = await loadFixture(deployEscrow);
        expect(await escrow.buyer()).to.equal(buyer.address);
    });

    it("Should correctly set the itemPrice", async function () {
        const { escrow, itemPrice } = await loadFixture(deployEscrow);
        expect(await escrow.itemPrice()).to.equal(itemPrice);
    });

    it("Should correctly set the contractCreationDate", async function () {
        const { escrow } = await loadFixture(deployEscrow);
        const expectedCreationDate = await ethers.provider.getBlock('latest').then(block => block.timestamp);
        expect(await escrow.contractCreationDate()).to.equal(expectedCreationDate);
    });

    it("Should correctly set the deadlineForInitialDeposits", async function () {
        const { escrow } = await loadFixture(deployEscrow);
        const expectedCreationDate = await ethers.provider.getBlock('latest').then(block => block.timestamp);
        const expectedDeadline = expectedCreationDate + TWO_HOURS_IN_SECS;
        expect(await escrow.deadlineForInitialDeposits()).to.equal(expectedDeadline);
    });
});
