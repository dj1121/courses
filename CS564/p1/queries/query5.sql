SELECT COUNT(*)
FROM (
    SELECT *
    FROM Item INNER JOIN User on Item.SellerID = User.userID
    WHERE Rating > 1000
    GROUP BY Item.SellerID
);