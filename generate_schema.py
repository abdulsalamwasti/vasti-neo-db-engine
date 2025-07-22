import yaml
with open('schema/schema.yaml', 'r') as f:
    schema = yaml.safe_load(f)
with open('schema/constraints.cypher', 'w') as f:
    for constraint in schema['constraints']:
        f.write(f"CREATE CONSTRAINT IF NOT EXISTS FOR (n:{constraint['label']}) REQUIRE n.{constraint['property']} IS UNIQUE;\n")