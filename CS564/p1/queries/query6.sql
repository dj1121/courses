SELECT COUNT(*)
FROM (
    SELECT *
    FROM Item INNER JOIN Bid ON Item.SellerID = Bid.BidderID
    GROUP BY Item.SellerID
);