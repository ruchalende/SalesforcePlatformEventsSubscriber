package com.example.demo.salesforce.parser;

import com.salesforce.eventbus.protobuf.ConsumerEvent;
import org.apache.avro.Schema;
import org.apache.avro.file.SeekableByteArrayInput;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class EventDataParser {
    private final DatumReader<GenericRecord> datumReader;
    private static final Logger logger = LoggerFactory.getLogger(EventDataParser.class);

    public EventDataParser(Schema schema) {
        this.datumReader = new GenericDatumReader<>(schema);
    }

    public SfPlatformEvent parse(ConsumerEvent event) {
        SeekableByteArrayInput byteArray = new SeekableByteArrayInput(event.getEvent().getPayload().toByteArray());
        BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(byteArray, null);
        try {
            GenericData.Record eventPayload = (GenericData.Record) datumReader.read(null, decoder);
            return new SfPlatformEvent(eventPayload);
        } catch (IOException e) {
            logger.error("Failed to parse message: {}", e.getMessage(), e);
            return null;
        }
    }
}

