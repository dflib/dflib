# UPGRADE INSTRUCTIONS

## 0.6

* [dflib #54](https://github.com/bootique/bootique-agrest/issues/37): 
`Aggregator.first()` used to return the first **non-null** element from 
a column, which is counter-intuitive. It is changed to return the first 
element whether it is null or not.