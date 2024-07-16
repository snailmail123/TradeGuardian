const { expect } = require("chai");
const {
    loadFixture,
    deployEscrow,
    EscrowStates,
    BUYER_DEPOSIT_MULTIPLIER
} = require("./utils");

describe("Withdraw", function () {
    it("Should revert if called by a non-party", async function () {
        const { escrow, thirdParty } = await loadFixture(deployEscrow);

        await expect(escrow.connect(thirdParty).withdraw())
            .to.be.revertedWith("You are not the buyer or seller.");
    });

    it("Should revert if the state is not AWAITING_DEPOSIT", async function () {
        const { escrow, buyer, seller, itemPrice } = await loadFixture(deployEscrow);
        const buyerDepositAmount = itemPrice * BUYER_DEPOSIT_MULTIPLIER;
        const sellerDepositAmount = itemPrice;

        //Change to a different state from AWAITING_DEPOSIT
        await escrow.connect(seller).sellerDeposit({ value: sellerDepositAmount });
        await escrow.connect(buyer).buyerDeposit({ value: buyerDepositAmount });

        await expect(escrow.connect(buyer).withdraw())
            .to.be.revertedWith("Not in the AWAITING_DEPOSIT state");
    });

    it("Should revert if trying to withdraw one second before the deadline", async function () {
        const { escrow, seller, itemPrice } = await loadFixture(deployEscrow);

        await escrow.connect(seller).sellerDeposit({ value: itemPrice });

        const currentTimestamp = await ethers.provider.getBlock('latest').then(block => block.timestamp);
        const deadline = await escrow.deadlineForInitialDeposits();

        const fiveSecondsBeforeDeadline = BigInt(deadline) - 5n;
        const secondsToIncrease = fiveSecondsBeforeDeadline - BigInt(currentTimestamp);

        await network.provider.send("evm_increaseTime", [Number(secondsToIncrease)]);
        await network.provider.send("evm_mine"); //Mine new block to adjust time

        await expect(escrow.connect(seller).withdraw())
            .to.be.revertedWith("Cannot withdraw before two hours after contract creation");
    });

    it("Should revert if a user without funds tries to withdraw", async function() {
        const { escrow, buyer, seller, itemPrice } = await loadFixture(deployEscrow);
    
        await escrow.connect(seller).sellerDeposit({ value: itemPrice });
    
        // Wait for the deadline to pass to ensure withdrawal is possible.
        const currentTimestamp = await ethers.provider.getBlock('latest').then(block => block.timestamp);
        const deadline = await escrow.deadlineForInitialDeposits();
    
        const fiveSecondsAfterDeadline = BigInt(deadline) + 5n;
        const secondsToIncrease = fiveSecondsAfterDeadline - BigInt(currentTimestamp);
    
        await network.provider.send("evm_increaseTime", [Number(secondsToIncrease)]);
        await network.provider.send("evm_mine");
    
        // Buyer (who hasn't deposited) tries to withdraw.
        await expect(escrow.connect(buyer).withdraw())
            .to.be.revertedWith("No funds to withdraw");
    });

    it("Should set the state to CANCELED after successful withdrawal", async function() {
        const { escrow, seller, itemPrice } = await loadFixture(deployEscrow);
    
        await escrow.connect(seller).sellerDeposit({ value: itemPrice });
    
        const currentTimestamp = await ethers.provider.getBlock('latest').then(block => block.timestamp);
        const deadline = await escrow.deadlineForInitialDeposits();
    
        const fiveSecondsAfterDeadline = BigInt(deadline) + 5n;
        const secondsToIncrease = fiveSecondsAfterDeadline - BigInt(currentTimestamp);
    
        await network.provider.send("evm_increaseTime", [Number(secondsToIncrease)]);
        await network.provider.send("evm_mine");
    
        await escrow.connect(seller).withdraw();
    
        expect(await escrow.currentState()).to.equal(EscrowStates.CANCELED);
    });
    
    it("Should emit a TradeCanceled event after successful withdrawal", async function() {
        const { escrow, seller, itemPrice } = await loadFixture(deployEscrow);
    
        await escrow.connect(seller).sellerDeposit({ value: itemPrice });
    
        const currentTimestamp = await ethers.provider.getBlock('latest').then(block => block.timestamp);
        const deadline = await escrow.deadlineForInitialDeposits();
    
        const fiveSecondsAfterDeadline = BigInt(deadline) + 5n;
        const secondsToIncrease = fiveSecondsAfterDeadline - BigInt(currentTimestamp);
    
        await network.provider.send("evm_increaseTime", [Number(secondsToIncrease)]);
        await network.provider.send("evm_mine");
    
        const withdrawTx = await escrow.connect(seller).withdraw();
    
        await expect(withdrawTx)
            .to.emit(escrow, 'TradeCanceled')
            .withArgs(seller.address, itemPrice);  
    });
    
});