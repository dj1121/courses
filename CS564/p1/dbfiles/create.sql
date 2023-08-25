drop table if exists Item;
drop table if exists Category;
drop table if exists User;
drop table if exists Bid;

create table Item(
    ItemID int not null,
    SellerID int not null,
    ItemName text not null,
    Currently double not null,
    Buy_Price double,
    First_Bid double not null,
    Num_Bids int not null,
    Started text not null,
    Ends text not null,
    Description text,
    primary key (ItemID),
    foreign key (SellerID) references User(UserID)
);
create table Category(
    ItemID int not null,
    Category text not null,
    primary key (ItemID, Category),
    foreign key (ItemID) references Item(ItemID)
);
create table User(
    UserID int not null,
    Rating double not null,
    Location text,
    Country text,
    primary key (UserID)
);
create table Bid(
    ItemID int not null,
    BidderID int not null,
    Amount double not null,
    Time text not null,
    primary key (ItemID, BidderID, Amount)
    foreign key (ItemID) references Item(ItemID),
    foreign key (BidderID) references User(UserID)
);


