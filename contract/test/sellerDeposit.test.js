const { expect } = require("chai");
const { 
    loadFixture,
    deployEscrow, 
    EscrowStates, 
    BUYER_DEPOSIT_MULTIPLIER,
    ONE_WEI 
} = require("./utils");

describe("Seller Deposit", function() {
    it("Should revert if the state is not AWAITING_DEPOSIT", async function () {
        const { escrow, buyer, seller, itemPrice } = await loadFixture(deployEscrow);
        const buyerDepositAmount = itemPrice * BUYER_DEPOSIT_MULTIPLIER;
        const sellerDepositAmount = itemPrice;

        //Change to a different state from AWAITING_DEPOSIT
        await escrow.connect(seller).sellerDeposit({ value: sellerDepositAmount });
        await escrow.connect(buyer).buyerDeposit({ value: buyerDepositAmount });

        const depositAmount = itemPrice * BUYER_DEPOSIT_MULTIPLIER;
        await expect(escrow.connect(seller).sellerDeposit({ value: depositAmount }))
            .to.be.revertedWith("Invalid state for this action");
    });

    it("Should revert if the seller has already deposited", async function () {
        const { escrow, seller, itemPrice } = await loadFixture(deployEscrow);
        const sellerDepositAmount = itemPrice;

        // First deposit from seller
        await escrow.connect(seller).sellerDeposit({ value: sellerDepositAmount });

        // Attempt a second deposit from seller
        await expect(escrow.connect(seller).sellerDeposit({ value: sellerDepositAmount }))
            .to.be.revertedWith("Seller has already deposited");
    });

    it("Should not allow non-seller to deposit", async function () {
        const { escrow, buyer, itemPrice } = await loadFixture(deployEscrow);
        const depositAmount = itemPrice * BUYER_DEPOSIT_MULTIPLIER;

        await expect(escrow.connect(buyer).sellerDeposit({
            value: depositAmount
        }))
            .to.be.revertedWith("Only seller can execute this");
    });

    it("Should revert if seller deposits 1 wei above the correct amount", async function () {
        const { escrow, seller, itemPrice } = await loadFixture(deployEscrow);
        const depositAmountAbove = itemPrice + ONE_WEI;

        await expect(escrow.connect(seller).sellerDeposit({
            value: depositAmountAbove
        }))
            .to.be.revertedWith("Amount sent must be exactly equal to the item price");
    });

    it("Should revert if seller deposits 1 wei below the correct amount", async function () {
        const { escrow, seller, itemPrice } = await loadFixture(deployEscrow);
        const depositAmountBelow = itemPrice - ONE_WEI;

        await expect(escrow.connect(seller).sellerDeposit({
            value: depositAmountBelow
        }))
            .to.be.revertedWith("Amount sent must be exactly equal to the item price");
    });

    it("Should allow seller to deposit correct amount", async function () {
        const { escrow, seller, itemPrice } = await loadFixture(deployEscrow);
        const depositAmount = itemPrice; 
    
        await escrow.connect(seller).sellerDeposit({ value: depositAmount });
    
        const sellerBalance = await escrow.userBalances(seller.address);
        expect(sellerBalance).to.equal(depositAmount);
    });
    
    it("Should remain in AWAITING_DEPOSIT state if only seller has deposited", async function () {
        const { escrow, seller, itemPrice } = await loadFixture(deployEscrow);
        const sellerDepositAmount = itemPrice;

        await escrow.connect(seller).sellerDeposit({ value: sellerDepositAmount });

        const currentState = await escrow.currentState();
        expect(currentState).to.equal(EscrowStates.AWAITING_DEPOSIT);
    });

    it("Should transition to AWAITING_DELIVERY after both seller and buyer deposit", async function () {
        const { escrow, buyer, seller, itemPrice } = await loadFixture(deployEscrow);
        const buyerDepositAmount = itemPrice * BUYER_DEPOSIT_MULTIPLIER;
        const sellerDepositAmount = itemPrice;

        await escrow.connect(buyer).buyerDeposit({ value: buyerDepositAmount });
        await escrow.connect(seller).sellerDeposit({ value: sellerDepositAmount });

        const currentState = await escrow.currentState();
        expect(currentState).to.equal(EscrowStates.AWAITING_DELIVERY);
    });

    it("Should emit SellerDeposited event with correct parameters", async function () {
        const { escrow, seller, itemPrice } = await loadFixture(deployEscrow);
        const depositAmount = itemPrice; 
    
        await expect(escrow.connect(seller).sellerDeposit({ value: depositAmount }))
            .to.emit(escrow, 'SellerDeposited')
            .withArgs(seller.address, depositAmount);
    })    
});
