const { expect } = require("chai");
const { 
    loadFixture,
    deployEscrow, 
    BUYER_DEPOSIT_MULTIPLIER,
    EscrowStates
} = require("./utils");

describe("Set seller has given item", function () {

    it("Should revert if non-seller tries to mark item as sent", async function () {
        const { escrow, buyer, seller, itemPrice } = await loadFixture(deployEscrow);
        const buyerDepositAmount = itemPrice * BUYER_DEPOSIT_MULTIPLIER;
        const sellerDepositAmount = itemPrice;

        await escrow.connect(buyer).buyerDeposit({ value: buyerDepositAmount });
        await escrow.connect(seller).sellerDeposit({ value: sellerDepositAmount });

        await expect(escrow.connect(buyer).setSellerHasGivenItem()).to.be.revertedWith("Only seller can execute this");
    });

    it("Should revert if the state is not AWAITING_DELIVERY", async function () {
        const { escrow, seller } = await loadFixture(deployEscrow);
        await expect(escrow.connect(seller).setSellerHasGivenItem()).to.be.revertedWith("Invalid state for this action");
    });

    it("Should allow seller to mark item as sent when in AWAITING_DELIVERY state", async function () {
        const { escrow, buyer, seller, itemPrice } = await loadFixture(deployEscrow);
        const buyerDepositAmount = itemPrice * BUYER_DEPOSIT_MULTIPLIER;
        const sellerDepositAmount = itemPrice;

        await escrow.connect(buyer).buyerDeposit({ value: buyerDepositAmount });
        await escrow.connect(seller).sellerDeposit({ value: sellerDepositAmount });
        await escrow.connect(seller).setSellerHasGivenItem();
            
        const currentState = await escrow.currentState();
        expect(currentState).to.equal(EscrowStates.ITEM_SENT);
    });

    it("Should emit ItemSent event with correct parameters", async function () {
        const { escrow, buyer, seller, itemPrice } = await loadFixture(deployEscrow);
        const buyerDepositAmount = itemPrice * BUYER_DEPOSIT_MULTIPLIER;
        const sellerDepositAmount = itemPrice;
    
        await escrow.connect(buyer).buyerDeposit({ value: buyerDepositAmount });
        await escrow.connect(seller).sellerDeposit({ value: sellerDepositAmount });
    
        await expect(escrow.connect(seller).setSellerHasGivenItem())
            .to.emit(escrow, 'ItemSent')
            .withArgs(seller.address);
    });    
});