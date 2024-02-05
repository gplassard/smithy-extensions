package fr.gplassard.smithyextensions.openapi.mappers;

import software.amazon.smithy.jsonschema.JsonSchemaMapper;
import software.amazon.smithy.jsonschema.JsonSchemaMapperContext;
import software.amazon.smithy.jsonschema.Schema;
import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.node.ObjectNode;
import software.amazon.smithy.model.node.StringNode;

import java.util.Optional;

public class OpenApiTypeMapper implements JsonSchemaMapper {

    @Override
    public Schema.Builder updateSchema(JsonSchemaMapperContext context, Schema.Builder schemaBuilder) {
        var openApiTrait = context.getShape().getAllTraits().values().stream().filter(t -> t.toShapeId().getNamespace().equals("openapiplugin") && t.toShapeId().getName().equals("openApiType")).findFirst();

        if (openApiTrait.isEmpty()) {
            return JsonSchemaMapper.super.updateSchema(context, schemaBuilder);
        }
        var objectNode = openApiTrait.get().toNode().asObjectNode();
        var type = extractValue(objectNode, "type");
        var format = extractValue(objectNode, "format");
        if (type.isEmpty()) {
            throw new RuntimeException("Invalid @openApiType, could not retrieve type");
        }
        return JsonSchemaMapper.super.updateSchema(context, schemaBuilder).type(type.get()).format(format.orElse(null));
    }

    private static Optional<String> extractValue(Optional<ObjectNode> objectNode, String attribute) {
        return objectNode.map(ObjectNode::getStringMap)
                .flatMap(t -> Optional.ofNullable(t.get(attribute)))
                .flatMap(Node::asStringNode)
                .map(StringNode::getValue);
    }
}
