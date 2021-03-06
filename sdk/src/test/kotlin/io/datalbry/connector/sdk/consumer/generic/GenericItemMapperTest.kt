package io.datalbry.connector.sdk.consumer.generic

import io.datalbry.connector.sdk.consumer.generic.documents.*
import io.datalbry.precise.api.schema.document.Record
import io.datalbry.precise.api.schema.document.generic.GenericDocument
import io.datalbry.precise.api.schema.document.generic.GenericField
import io.datalbry.precise.api.schema.document.generic.GenericRecord
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.net.URI
import java.time.ZonedDateTime
import java.util.*

internal class GenericItemMapperTest {

    @Test
    fun getDocuments_mapSingleItem_workJustFine() {
        val item = SimpleDocument("unique id", "TestItem", "Just me...", ZonedDateTime.now().toString())
        val mapper = GenericItemMapper(item::class)

        val document = mapper.getDocuments(item).first()
        val id = UUID.nameUUIDFromBytes(item.docId.toByteArray()).toString()
        assertTrue(document.fields.isNotEmpty())
        assertEquals(id, document.id)
        assertEquals(document[SimpleDocument::title.name].value, item.title)
        assertEquals(document[SimpleDocument::author.name].value, item.author)
        assertEquals(document[SimpleDocument::modified.name].value, item.modified)
    }

    @Test
    fun getDocuments_mapSingleItemWithExcludedProperty_fieldIsNotPresent() {
        val item = DocumentWithExcludedProperty("unique id", "TestItem", "Just me...", ZonedDateTime.now().toString())
        val mapper = GenericItemMapper(item::class)

        val document = mapper.getDocuments(item).first()
        val id = UUID.nameUUIDFromBytes(item.docId.toByteArray()).toString()
        assertTrue(document.fields.isNotEmpty())
        assertEquals(id, document.id)
        assertEquals(document[DocumentWithExcludedProperty::title.name].value, item.title)
        assertEquals(document[DocumentWithExcludedProperty::modified.name].value, item.modified)
        assertThrows<NoSuchElementException> { document[DocumentWithExcludedProperty::child.name].value }
    }

    @Test
    fun getDocuments_withoutIdAddsSyntheticId_workJustFine() {
        val item = DocumentWithoutId("TestItem", "Just me...", ZonedDateTime.now().toString())
        val mapper = GenericItemMapper(item::class)

        val document = mapper.getDocuments(item).first()
        assertTrue(document.fields.isNotEmpty())
        assertNotNull(document.id)
        assertEquals(document[DocumentWithoutId::title.name].value, item.title)
        assertEquals(document[DocumentWithoutId::author.name].value, item.author)
        assertEquals(document[DocumentWithoutId::modified.name].value, item.modified)
    }

    @Test
    fun getDocuments_documentContainsCollection_workJustFine() {
        val item =
            DocumentWithCollection("another unique id", "Collection Item", listOf(""), ZonedDateTime.now().toString())
        val mapper = GenericItemMapper(item::class)

        val document = mapper.getDocuments(item).first()
        val id = UUID.nameUUIDFromBytes(item.docId.toByteArray()).toString()
        assertTrue(document.fields.isNotEmpty())
        assertEquals(document.id, id)
        assertEquals(document[DocumentWithCollection::title.name].value, item.title)
        assertEquals(document[DocumentWithCollection::contributors.name].value, item.contributors)
        assertEquals(document[DocumentWithCollection::modified.name].value, item.modified)
    }

    @Test
    fun getDocuments_containingCollectionOfIds_gettingReducedCorrectly() {
        val item = DocumentWithIdCollection(
            listOf("id1", "id2"),
            "Collection Item",
            listOf(""),
            ZonedDateTime.now().toString()
        )
        val mapper = GenericItemMapper(item::class)

        val document = mapper.getDocuments(item).first()
        val id = UUID.nameUUIDFromBytes(item.docId.toString().toByteArray()).toString()
        assertTrue(document.fields.isNotEmpty())
        assertEquals(id, document.id)
        assertEquals(document[DocumentWithCollection::title.name].value, item.title)
        assertEquals(document[DocumentWithCollection::contributors.name].value, item.contributors)
        assertEquals(document[DocumentWithCollection::modified.name].value, item.modified)
    }

    @Test
    fun getDocuments_children_haveNoEffectOnGetDocumentsChildren() {
        val childrenInput = listOf(Child("google", URI.create("http://google.com")))
        val item = DocumentWithChildren("id", "Collection Item", ZonedDateTime.now().toString(), childrenInput)
        val mapper = GenericItemMapper(item::class)

        val document = mapper.getDocuments(item).first()
        val id = UUID.nameUUIDFromBytes(item.docId.toByteArray()).toString()
        assertTrue(document.fields.isNotEmpty())
        assertEquals(document.id, id)
        assertEquals(document[DocumentWithChildren::title.name].value, item.title)
        assertEquals(document[DocumentWithChildren::modified.name].value, item.modified)

        val children = mapper.getEdges(item)
        val sourceEdges = childrenInput.map(Child::toEdge)
        assertTrue(children.containsAll(sourceEdges))
    }

    @Test
    fun getDocuments_deconstructingComplexFields_areMappedToRecords() {
        val item = DocumentWithComplexProperty(
            "id",
            "Collection Item",
            Person("test", "test@example.org"),
            ZonedDateTime.now().toString()
        )
        val mapper = GenericItemMapper(item::class)

        val document = mapper.getDocuments(item).first()
        val id = UUID.nameUUIDFromBytes(item.docId.toByteArray()).toString()
        assertTrue(document.fields.isNotEmpty())
        assertEquals(document.id, id)
        assertEquals(document[DocumentWithComplexProperty::title.name].value, item.title)
        assertEquals(document[DocumentWithComplexProperty::modified.name].value, item.modified)
        val author = document[DocumentWithComplexProperty::author.name].value
        assertTrue(author is Record)
        if (author is Record) {
            assertEquals(author[Person::mail.name].value, item.author.mail)
            assertEquals(author[Person::name.name].value, item.author.name)
        }

    }

    @Test
    fun getEdges_containingChildren_areMappedCorrectly() {
        val childrenInput = listOf(Child("google", URI.create("http://google.com")))
        val container = ContainerWithChildren(children = childrenInput)
        val mapper = GenericItemMapper(container::class)

        val documents = mapper.getDocuments(container)
        assertTrue(documents.isEmpty())

        val children = mapper.getEdges(container)
        val sourceEdges = childrenInput.map(Child::toEdge)
        assertTrue(children.containsAll(sourceEdges))
    }

    @Test
    fun getDocument_mappingOptionalEmptySimpleProperty_correctlyMapped() {
        val item = DocumentWithOptionalSimpleProperty(
            1,
            Optional.empty(),
            Optional.empty(),
            TestRecordWithOptionalSimpleProperty(
                1,
                Optional.of("TestString"),
                Optional.of(1),
            )
        )
        val mapper = GenericItemMapper(DocumentWithOptionalSimpleProperty::class)
        val document = mapper.getDocuments(item).first()
        val expected = GenericDocument(
            type = "DocumentWithOptionalSimpleProperty",
            id = UUID.nameUUIDFromBytes(item.id.toString().toByteArray()).toString(),
            fields = setOf(
                GenericField("id", 1),
                GenericField("optionalInt", Optional.empty<Int>()),
                GenericField("optionalString", Optional.empty<String>()),
                GenericField(
                    "testRecordWithOptionalSimpleProperty",
                    GenericRecord(
                        type = "TestRecordWithOptionalSimpleProperty",
                        setOf(
                            GenericField("id", 1),
                            GenericField("optionalInt", Optional.of(1)),
                            GenericField("optionalString", Optional.of("TestString")),
                        )
                    )
                ),
                GenericField("_checksum", "")
            )
        )
        assertEquals(expected, document)
    }

    @Test
    fun getDocument_mappingOptionalEmptyComplexProperty_correctlyMapped() {
        val item = DocumentWithOptionalComplexProperty(1, Optional.empty(), Optional.empty(), Optional.empty())
        val mapper = GenericItemMapper(DocumentWithOptionalComplexProperty::class)
        val document = mapper.getDocuments(item).first()
        val expected = GenericDocument(
            type = "DocumentWithOptionalComplexProperty",
            id = UUID.nameUUIDFromBytes(item.id.toString().toByteArray()).toString(),
            fields = setOf(
                GenericField("id", 1),
                GenericField("optionalInt", Optional.empty<Int>()),
                GenericField("optionalString", Optional.empty<String>()),
                GenericField(
                    "testRecordWithOptionalRecord",
                    Optional.empty<TestRecordWithOptionalRecord>()
                ),
                GenericField("_checksum", "")
            )
        )
        assertEquals(expected, document)
    }

    @Test
    fun getDocument_mappingOptionalComplexProperty_correctlyMapped() {
        val item = DocumentWithOptionalComplexProperty(
            1,
            Optional.of("TestString"),
            Optional.of(1),
            Optional.of(
                TestRecordWithOptionalRecord(
                    1,
                    Optional.empty()
                )
            )
        )
        val mapper = GenericItemMapper(DocumentWithOptionalComplexProperty::class)
        val document = mapper.getDocuments(item).first()
        val expected = GenericDocument(
            type = "DocumentWithOptionalComplexProperty",
            id = UUID.nameUUIDFromBytes(item.id.toString().toByteArray()).toString(),
            fields = setOf(
                GenericField("id", 1),
                GenericField("optionalInt", Optional.of(1)),
                GenericField("optionalString", Optional.of("TestString")),
                GenericField(
                    "testRecordWithOptionalRecord",
                    Optional.of(
                        GenericRecord(
                            "TestRecordWithOptionalRecord",
                            setOf(
                                GenericField("id", 1),
                                GenericField("testRecord", Optional.empty<TestRecord>()),
                            )
                        )
                    )
                ),
                GenericField("_checksum", "")
            )
        )
        assertEquals(expected, document)
    }

    @Test
    fun getDocument_mappingNullableSimpleProperty_correctlyMapped() {
        val item = DocumentWithNullableSimpleProperty(
            1,
            null,
            null,
            TestRecordWithNullableSimpleProperty(
                1,
                null,
                null
            )
        )
        val mapper = GenericItemMapper(DocumentWithNullableSimpleProperty::class)
        val document = mapper.getDocuments(item).first()
        println("")
    }
}

