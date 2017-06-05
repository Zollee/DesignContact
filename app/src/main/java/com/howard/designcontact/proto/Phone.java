// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: PersonMsg.proto at 15:1
package com.howard.designcontact.proto;

import com.squareup.wire.FieldEncoding;
import com.squareup.wire.Message;
import com.squareup.wire.ProtoAdapter;
import com.squareup.wire.ProtoReader;
import com.squareup.wire.ProtoWriter;
import com.squareup.wire.WireField;
import com.squareup.wire.internal.Internal;

import java.io.IOException;

import okio.ByteString;

public final class Phone extends Message<Phone, Phone.Builder> {
    public static final ProtoAdapter<Phone> ADAPTER = new ProtoAdapter_Phone();
    public static final Integer DEFAULT_ID = 0;
    public static final Integer DEFAULT_NAMEID = 0;
    public static final String DEFAULT_NUMBER = "";
    public static final Integer DEFAULT_TYPE = 0;
    private static final long serialVersionUID = 0L;
    @WireField(
            tag = 1,
            adapter = "com.squareup.wire.ProtoAdapter#INT32",
            label = WireField.Label.REQUIRED
    )
    public final Integer id;

    @WireField(
            tag = 2,
            adapter = "com.squareup.wire.ProtoAdapter#INT32",
            label = WireField.Label.REQUIRED
    )
    public final Integer nameId;

    @WireField(
            tag = 3,
            adapter = "com.squareup.wire.ProtoAdapter#STRING",
            label = WireField.Label.REQUIRED
    )
    public final String number;

    @WireField(
            tag = 4,
            adapter = "com.squareup.wire.ProtoAdapter#INT32",
            label = WireField.Label.REQUIRED
    )
    public final Integer type;

    public Phone(Integer id, Integer nameId, String number, Integer type) {
        this(id, nameId, number, type, ByteString.EMPTY);
    }

    public Phone(Integer id, Integer nameId, String number, Integer type, ByteString unknownFields) {
        super(ADAPTER, unknownFields);
        this.id = id;
        this.nameId = nameId;
        this.number = number;
        this.type = type;
    }

    @Override
    public Builder newBuilder() {
        Builder builder = new Builder();
        builder.id = id;
        builder.nameId = nameId;
        builder.number = number;
        builder.type = type;
        builder.addUnknownFields(unknownFields());
        return builder;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (!(other instanceof Phone)) return false;
        Phone o = (Phone) other;
        return unknownFields().equals(o.unknownFields())
                && id.equals(o.id)
                && nameId.equals(o.nameId)
                && number.equals(o.number)
                && type.equals(o.type);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode;
        if (result == 0) {
            result = unknownFields().hashCode();
            result = result * 37 + id.hashCode();
            result = result * 37 + nameId.hashCode();
            result = result * 37 + number.hashCode();
            result = result * 37 + type.hashCode();
            super.hashCode = result;
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(", id=").append(id);
        builder.append(", nameId=").append(nameId);
        builder.append(", number=").append(number);
        builder.append(", type=").append(type);
        return builder.replace(0, 2, "Phone{").append('}').toString();
    }

    public static final class Builder extends Message.Builder<Phone, Builder> {
        public Integer id;

        public Integer nameId;

        public String number;

        public Integer type;

        public Builder() {
        }

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder nameId(Integer nameId) {
            this.nameId = nameId;
            return this;
        }

        public Builder number(String number) {
            this.number = number;
            return this;
        }

        public Builder type(Integer type) {
            this.type = type;
            return this;
        }

        @Override
        public Phone build() {
            if (id == null
                    || nameId == null
                    || number == null
                    || type == null) {
                throw Internal.missingRequiredFields(id, "id",
                        nameId, "nameId",
                        number, "number",
                        type, "type");
            }
            return new Phone(id, nameId, number, type, super.buildUnknownFields());
        }
    }

    private static final class ProtoAdapter_Phone extends ProtoAdapter<Phone> {
        ProtoAdapter_Phone() {
            super(FieldEncoding.LENGTH_DELIMITED, Phone.class);
        }

        @Override
        public int encodedSize(Phone value) {
            return ProtoAdapter.INT32.encodedSizeWithTag(1, value.id)
                    + ProtoAdapter.INT32.encodedSizeWithTag(2, value.nameId)
                    + ProtoAdapter.STRING.encodedSizeWithTag(3, value.number)
                    + ProtoAdapter.INT32.encodedSizeWithTag(4, value.type)
                    + value.unknownFields().size();
        }

        @Override
        public void encode(ProtoWriter writer, Phone value) throws IOException {
            ProtoAdapter.INT32.encodeWithTag(writer, 1, value.id);
            ProtoAdapter.INT32.encodeWithTag(writer, 2, value.nameId);
            ProtoAdapter.STRING.encodeWithTag(writer, 3, value.number);
            ProtoAdapter.INT32.encodeWithTag(writer, 4, value.type);
            writer.writeBytes(value.unknownFields());
        }

        @Override
        public Phone decode(ProtoReader reader) throws IOException {
            Builder builder = new Builder();
            long token = reader.beginMessage();
            for (int tag; (tag = reader.nextTag()) != -1; ) {
                switch (tag) {
                    case 1:
                        builder.id(ProtoAdapter.INT32.decode(reader));
                        break;
                    case 2:
                        builder.nameId(ProtoAdapter.INT32.decode(reader));
                        break;
                    case 3:
                        builder.number(ProtoAdapter.STRING.decode(reader));
                        break;
                    case 4:
                        builder.type(ProtoAdapter.INT32.decode(reader));
                        break;
                    default: {
                        FieldEncoding fieldEncoding = reader.peekFieldEncoding();
                        Object value = fieldEncoding.rawProtoAdapter().decode(reader);
                        builder.addUnknownField(tag, fieldEncoding, value);
                    }
                }
            }
            reader.endMessage(token);
            return builder.build();
        }

        @Override
        public Phone redact(Phone value) {
            Builder builder = value.newBuilder();
            builder.clearUnknownFields();
            return builder.build();
        }
    }
}
