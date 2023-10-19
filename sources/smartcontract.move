module tc::TC {

    use sui::coin::{Self, Coin};
    use sui::balance::{Self, Balance};
    use sui::event;
    use sui::object::{Self, ID, UID};
    use sui::math;
    use sui::sui::SUI;
    use sui::transfer;
    use sui::tx_context::{Self, TxContext};
    use std::option::{Self, Option};
    use sui::dynamic_field as dfield;
    use sui::dynamic_object_field as ofield;
    use sui::address;
    use 0x2::kiosk;
    use sui::bag::Bag;
    use std::string;
    use sui::clock::{Self, Clock};
    
    const CLOCK: u64 = 0x6;

    

    // Trusted contract between users
    struct TrustedContract has key, store  {
        id: UID,
        users: vector<address>,
        creationTime: u64,
        //userA: address, > Add addresses via dynamic fields ?
        description : string::String,
        conditions: Conditions,
        authorizations: Authorizations,

    }

    //--------------------------- AUTORIZATIONS -------------------------

    struct Authorizations has store{
        autorizations: vector<AuthorizationStep>,
    }

    // Capability to use certain functions of the smart contract about a certain TC at a certain step
    struct AuthorizationStep has store  {
        idContract: UID,
        authorizationType: string::String,
        atwhichStep : u64,
    }

    // Confirmation key to confirm the contract for real life use
    struct ConfirmationKey{
        id: UID,
        uidOfTheContract: UID,
        users: vector<address>,
    }

    // State of the progession of the contract, specifically the index of the vector of requirement-action which is currently dealt with. This keeps track of the evolution of the state of the contract.
    struct ContractState has key{
        id: UID,
        uidOfTheContract: UID,
        state: u64,
    }

    //--------------------------- ACTION -------------------------

    struct Action has store {
        uidOfTheContract: UID,
        doneActions : vector<string::String>, //to know the name of the fields that were succesfully done
        pendingActions : vector<string::String>,       
    }
    // SUB-ACTIONS

    struct SWAP_ITEMS has store{} // change "owners" of the objects (keys to access)
    struct GIVE_ITEM has store{} // change one "owner" (key)
    struct MODIFY_TIME_CONSTRAINTS has store{}
    struct UNLOCK_ITEM has store{}
    struct CANCEL has store{}


    //--------------------------- REQUIREMENT -------------------------
    struct Requirement has store{
        requirements : vector<string::String>, //to know the name of the fields that were succesfully done
    }

    //SUB-REQUIREMENTS TO BE ADDED TO A REQUIREMENT

    // Requires a specific Item (object)
    struct ItemRequirement{
        requestedItem: UID,

    }

    // Require a specific Item from a specific person (address)
    struct ItemRequirementFrom{
        requestedItem: UID,
        requestedSender: UID,
    }
    

    // Require a confirmation from a specific person (address)
    struct ConfirmationRequirementFrom{}

    struct ConfirmationRequirementFromWithKey{}

    struct LockedItemRequirement{}

    //--------------------------- REQUIREMENT-ACTION PAIR -------------------------

    struct Requirement_action has store{
        requirement: Requirement,
        action: Action,
    }

    //--------------------------- CONDITIONS OF A CONTRACT -------------------------

    struct Conditions has store{
        conditions: vector<Requirement_action>,
    }


    //--------------------------- FUNCTIONS -------------------------

    public entry fun new_contract(ctx: &mut TxContext, users: vector<address>,
        creationTime: u64,
        description : string::String,
        conditions: Conditions,
        authorizations: Authorizations){
        let newcontract = TrustedContract{
            id: object::new(ctx),
            users: users,
            creationTime: clock::timestamp_ms(0x6),
            //userA: address, > Add addresses via dynamic fields ?
            description : description,
            conditions: conditions,
            authorizations: authorizations,
        };

    }


        
    }