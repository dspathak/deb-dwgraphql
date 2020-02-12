package com.deb.dw.gql;

import static graphql.schema.idl.RuntimeWiring.newRuntimeWiring;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.StaticDataFetcher;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

public class HelloWorldGraphQlSampleApp {

    public static void main(String[] args) {

        // Use guava to convert schema file into a string. Below code throws IOException, so need to
        // handle it

        // URL url = Resources.getResource("schema.graphqls");
        // String schema = Resources.toString(url, Charsets.UTF_8);

        String schema = "type Query{hello: String}";

        GraphQLSchema graphQLSchema = buildSchema(schema);
        GraphQL graphQL = GraphQL.newGraphQL(graphQLSchema).build();

        ExecutionResult executionResult = graphQL.execute("{hello}");
        System.out.println(executionResult.getData().toString());
        // Prints: {hello=world}
    }

    private static GraphQLSchema buildSchema(String sdl) {

        SchemaParser schemaParser = new SchemaParser();
        TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(sdl);

        RuntimeWiring runtimeWiring = buildWiring();

        SchemaGenerator schemaGenerator = new SchemaGenerator();
        GraphQLSchema graphQLSchema =
                schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);
        return graphQLSchema;
    }

    private static RuntimeWiring buildWiring() {

        RuntimeWiring runtimeWiring = newRuntimeWiring()
                .type("Query",
                        builder -> builder.dataFetcher("hello", new StaticDataFetcher("world")))
                .build();
        return runtimeWiring;

        // return RuntimeWiring.newRuntimeWiring()
        // .type(newTypeWiring("Query")
        // .dataFetcher("bookById", graphQLDataFetchers.getBookByIdDataFetcher()))
        // .type(newTypeWiring("Book")
        // .dataFetcher("author", graphQLDataFetchers.getAuthorDataFetcher()))
        // .build();

    }

}
