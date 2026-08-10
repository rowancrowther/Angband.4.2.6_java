# Issues to deal with #

## Present code ##

[ ] 26-08-10-12-16 Moving `GameEventType` into channel is a bigger move than it looks. It's referenced across middle, so
it's a stage-0-style import churn on top of stage 1's new code. Worth doing as its own commit before the records, so a
compile failure tells you which of the two broke.
