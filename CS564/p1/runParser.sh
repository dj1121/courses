python parser.py ebay_data/items-*.json
sort user.dat | uniq > user1.dat
sort category.dat | uniq > category1.dat
sort bid.dat | uniq > bid1.dat
sort item.dat | uniq > item1.dat
mv category1.dat category.dat
mv user1.dat user.dat
mv bid1.dat bid.dat
mv item1.dat item.dat