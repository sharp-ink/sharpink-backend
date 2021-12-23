# Testing the API the right way...

---

## Unit tests

- Services layer : the goal is to unit test the features provided by the services classes.  
DAOs should be mocked.
- Persistence layer : the goal is to unit test the DAOs.  
The database is a mocked DB (in-memory H2).
---

## Integration-like tests
- Controllers layer : the goal is to test the endpoints of the API.  
**NOTHING** should be mocked here, except the database => we use the same H2 database than for persistence unit tests.  
These tests should test the feature provided by an endpoint, so we should only write tests for the most common use cases, and not try to target a 100% coverage. 
