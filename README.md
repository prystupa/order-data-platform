rt-order-reporting
==================

[![Build Status](https://travis-ci.org/prystupa/rt-order-reporting.png)](https://travis-ci.org/prystupa/rt-order-reporting)

The 'rt-order-reporting' POC aims to quickly prototype a distributed realtime solution to a large volume (hundreds of millions of events daily) order reporting problem. Outstanding questions [here] (#outstanding-questions).

Order reporting include the following key steps:
  
  - Ingestion - collecting order events from trading systems (simulated in the POC)
  - Linking - organizing related order events in chains based on their parent/child relationship; think of a chain a potentially very large order trees (thousands of events)
  - Enrichment - computing additional properties for complete chains; enrichment can only be done when full chain is received; ideally it is done as soon as full chain is received; incomplete chains are processed on EOD marker
  - Record extraction and validation - running external set of rules to validate reported data before delivery
  - FTP delivery - writing validated reporting records to an FTP stream

The idea is to be able to scale inifinitly by processing events on a large distributed grid, in realtime. We also want to levarage data locality - we can partition related orders such that all orders from the same chain are stored and processed on the same node - to minimize network chatter.

Hazelcast is used in this POC as in-memory distributed grid provider.

Currently implemented ideas are documeted below.

## Data model
An order event is modeled as a simple tuple (ID, parent ID, partition key). Partition key is an attribute of an oder that guarantees orders from the same chain map to the same partition. Product ID (ticker) is an example of such a key in real life.

## Ingestion
We use a partition aware command executin (StoreCommand) to siubmit simulated order event to the grid. The command recieves an event tuple and executes on appropriate node. Upon execution it updates two maps:
- parents - ID -> parent ID association
- chains - multimap of (root ID -> ID)
When new event tuple arrives, Store command pessimistically assumes its parent ID is also its chain root ID

## Linking
We leverage Hazelcast event listeners to perform realtime chain linking as events arrive. There are two listeners:
- on *parents* map - every time new parent/child record is added listener kicks in and checks if there is a chain currently rooted at child ID. If there is then this chain is merged into parent ID chain
- on *chains* multimap - every time an event is added a listener kicks in and checks if the key is indeed a root. If not, the event is moved to its root

## Enrichment
TODO

## Outstanding questions
- Overall architecture/best practices - is the approach valid and optimal?
- Recovery - in the unfortunate event that two nodes are going down (primary node and its backup) at the same time, how would I even detect there is data loss? How do I know which keys are gone and need to be reprocessed?
- Ingestion speed and lack of back pressure - if clients feed events at a high rate, the server starts dropping events (with 'Event queue is overloaded' message). A test case we used is feeding 1mln events to a single Hazelcast node deplyed on m2.large EC2 instance
- Large memory consumption - feeding a million tuples to a single HZ server easily blows through 3.5G memory available on m2.large instance; this is somewhat unexpected based on math
