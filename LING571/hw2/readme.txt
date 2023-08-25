I had some troubles setting up Condor but it seems to work now. For some reason
it was not fully converting the atis.cfg to a full CNF grammar so my parses
were'nt fully complete when testing. My parses for my CNF grammar and my original 
grammar were exactly the same even though the grammar productions were different (as they should be).
After long hours of debugging the issue was due to the way I was fixing my long productions.
