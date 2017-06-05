// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: PersonMsg.proto at 7:1
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

public final class Person extends Message<Person, Person.Builder> {
    public static final ProtoAdapter<Person> ADAPTER = new ProtoAdapter_Person();
    public static final Integer DEFAULT_ID = 0;
    public static final String DEFAULT_NAME = "";
    public static final ByteString DEFAULT_PHOTOSMALL = ByteString.EMPTY;
    public static final ByteString DEFAULT_PHOTOLARGE = ByteString.EMPTY;
    public static final Integer DEFAULT_ISSTARRED = 0;
    private static final long serialVersionUID = 0L;
    @WireField(
            tag = 1,
            adapter = "com.squareup.wire.ProtoAdapter#INT32",
            label = WireField.Label.REQUIRED
    )
    public final Integer id;

    @WireField(
            tag = 2,
            adapter = "com.squareup.wire.ProtoAdapter#STRING",
            label = WireField.Label.REQUIRED
    )
    public final String name;

    @WireField(
            tag = 3,
            adapter = "com.squareup.wire.ProtoAdapter#BYTES"
    )
    public final ByteString photoSmall;

    @WireField(
            tag = 4,
            adapter = "com.squareup.wire.ProtoAdapter#BYTES"
    )
    public final ByteString photoLarge;

    @WireField(
            tag = 5,
            adapter = "com.squareup.wire.ProtoAdapter#INT32",
            label = WireField.Label.REQUIRED
    )
    public final Integer isStarred;

    public Person(Integer id, String name, ByteString photoSmall, ByteString photoLarge, Integer isStarred) {
        this(id, name, photoSmall, photoLarge, isStarred, ByteString.EMPTY);
    }

    public Person(Integer id, String name, ByteString photoSmall, ByteString photoLarge, Integer isStarred, ByteString unknownFields) {
        super(ADAPTER, unknownFields);
        this.id = id;
        this.name = name;
        this.photoSmall = photoSmall;
        this.photoLarge = photoLarge;
        this.isStarred = isStarred;
    }

    @Override
    public Builder newBuilder() {
        Builder builder = new Builder();
        builder.id = id;
        builder.name = name;
        builder.photoSmall = photoSmall;
        builder.photoLarge = photoLarge;
        builder.isStarred = isStarred;
        builder.addUnknownFields(unknownFields());
        return builder;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (!(other instanceof Person)) return false;
        Person o = (Person) other;
        return unknownFields().equals(o.unknownFields())
                && id.equals(o.id)
                && name.equals(o.name)
                && Internal.equals(photoSmall, o.photoSmall)
                && Internal.equals(photoLarge, o.photoLarge)
                && isStarred.equals(o.isStarred);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode;
        if (result == 0) {
            result = unknownFields().hashCode();
            result = result * 37 + id.hashCode();
            result = result * 37 + name.hashCode();
            result = result * 37 + (photoSmall != null ? photoSmall.hashCode() : 0);
            result = result * 37 + (photoLarge != null ? photoLarge.hashCode() : 0);
            result = result * 37 + isStarred.hashCode();
            super.hashCode = result;
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(", id=").append(id);
        builder.append(", name=").append(name);
        if (photoSmall != null) builder.append(", photoSmall=").append(photoSmall);
        if (photoLarge != null) builder.append(", photoLarge=").append(photoLarge);
        builder.append(", isStarred=").append(isStarred);
        return builder.replace(0, 2, "Person{").append('}').toString();
    }

    public static final class Builder extends Message.Builder<Person, Builder> {
        public Integer id;

        public String name;

        public ByteString photoSmall;

        public ByteString photoLarge;

        public Integer isStarred;

        public Builder() {
        }

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder photoSmall(ByteString photoSmall) {
            this.photoSmall = photoSmall;
            return this;
        }

        public Builder photoLarge(ByteString photoLarge) {
            this.photoLarge = photoLarge;
            return this;
        }

        public Builder isStarred(Integer isStarred) {
            this.isStarred = isStarred;
            return this;
        }

        @Override
        public Person build() {
            if (id == null
                    || name == null
                    || isStarred == null) {
                throw Internal.missingRequiredFields(id, "id",
                        name, "name",
                        isStarred, "isStarred");
            }
            return new Person(id, name, photoSmall, photoLarge, isStarred, super.buildUnknownFields());
        }
    }

    private static final class ProtoAdapter_Person extends ProtoAdapter<Person> {
        ProtoAdapter_Person() {
            super(FieldEncoding.LENGTH_DELIMITED, Person.class);
        }

        @Override
        public int encodedSize(Person value) {
            return ProtoAdapter.INT32.encodedSizeWithTag(1, value.id)
                    + ProtoAdapter.STRING.encodedSizeWithTag(2, value.name)
                    + (value.photoSmall != null ? ProtoAdapter.BYTES.encodedSizeWithTag(3, value.photoSmall) : 0)
                    + (value.photoLarge != null ? ProtoAdapter.BYTES.encodedSizeWithTag(4, value.photoLarge) : 0)
                    + ProtoAdapter.INT32.encodedSizeWithTag(5, value.isStarred)
                    + value.unknownFields().size();
        }

        @Override
        public void encode(ProtoWriter writer, Person value) throws IOException {
            ProtoAdapter.INT32.encodeWithTag(writer, 1, value.id);
            ProtoAdapter.STRING.encodeWithTag(writer, 2, value.name);
            if (value.photoSmall != null)
                ProtoAdapter.BYTES.encodeWithTag(writer, 3, value.photoSmall);
            if (value.photoLarge != null)
                ProtoAdapter.BYTES.encodeWithTag(writer, 4, value.photoLarge);
            ProtoAdapter.INT32.encodeWithTag(writer, 5, value.isStarred);
            writer.writeBytes(value.unknownFields());
        }

        @Override
        public Person decode(ProtoReader reader) throws IOException {
            Builder builder = new Builder();
            long token = reader.beginMessage();
            for (int tag; (tag = reader.nextTag()) != -1; ) {
                switch (tag) {
                    case 1:
                        builder.id(ProtoAdapter.INT32.decode(reader));
                        break;
                    case 2:
                        builder.name(ProtoAdapter.STRING.decode(reader));
                        break;
                    case 3:
                        builder.photoSmall(ProtoAdapter.BYTES.decode(reader));
                        break;
                    case 4:
                        builder.photoLarge(ProtoAdapter.BYTES.decode(reader));
                        break;
                    case 5:
                        builder.isStarred(ProtoAdapter.INT32.decode(reader));
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
        public Person redact(Person value) {
            Builder builder = value.newBuilder();
            builder.clearUnknownFields();
            return builder.build();
        }
    }
}
