// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: PersonMsg.proto at 22:1
package com.howard.designcontact.proto;

import com.squareup.wire.FieldEncoding;
import com.squareup.wire.Message;
import com.squareup.wire.ProtoAdapter;
import com.squareup.wire.ProtoReader;
import com.squareup.wire.ProtoWriter;
import com.squareup.wire.WireField;
import com.squareup.wire.internal.Internal;
import java.io.IOException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.util.List;
import okio.ByteString;

public final class Data extends Message<Data, Data.Builder> {
  public static final ProtoAdapter<Data> ADAPTER = new ProtoAdapter_Data();

  private static final long serialVersionUID = 0L;

  public static final String DEFAULT_USER = "";

  @WireField(
      tag = 1,
      adapter = "com.squareup.wire.ProtoAdapter#STRING",
      label = WireField.Label.REQUIRED
  )
  public final String user;

  @WireField(
      tag = 2,
      adapter = "com.howard.designcontact.proto.Person#ADAPTER",
      label = WireField.Label.REPEATED
  )
  public final List<Person> persons;

  @WireField(
      tag = 3,
      adapter = "com.howard.designcontact.proto.Phone#ADAPTER",
      label = WireField.Label.REPEATED
  )
  public final List<Phone> phoned;

  public Data(String user, List<Person> persons, List<Phone> phoned) {
    this(user, persons, phoned, ByteString.EMPTY);
  }

  public Data(String user, List<Person> persons, List<Phone> phoned, ByteString unknownFields) {
    super(ADAPTER, unknownFields);
    this.user = user;
    this.persons = Internal.immutableCopyOf("persons", persons);
    this.phoned = Internal.immutableCopyOf("phoned", phoned);
  }

  @Override
  public Builder newBuilder() {
    Builder builder = new Builder();
    builder.user = user;
    builder.persons = Internal.copyOf("persons", persons);
    builder.phoned = Internal.copyOf("phoned", phoned);
    builder.addUnknownFields(unknownFields());
    return builder;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof Data)) return false;
    Data o = (Data) other;
    return unknownFields().equals(o.unknownFields())
        && user.equals(o.user)
        && persons.equals(o.persons)
        && phoned.equals(o.phoned);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode;
    if (result == 0) {
      result = unknownFields().hashCode();
      result = result * 37 + user.hashCode();
      result = result * 37 + persons.hashCode();
      result = result * 37 + phoned.hashCode();
      super.hashCode = result;
    }
    return result;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(", user=").append(user);
    if (!persons.isEmpty()) builder.append(", persons=").append(persons);
    if (!phoned.isEmpty()) builder.append(", phoned=").append(phoned);
    return builder.replace(0, 2, "Data{").append('}').toString();
  }

  public static final class Builder extends Message.Builder<Data, Builder> {
    public String user;

    public List<Person> persons;

    public List<Phone> phoned;

    public Builder() {
      persons = Internal.newMutableList();
      phoned = Internal.newMutableList();
    }

    public Builder user(String user) {
      this.user = user;
      return this;
    }

    public Builder persons(List<Person> persons) {
      Internal.checkElementsNotNull(persons);
      this.persons = persons;
      return this;
    }

    public Builder phoned(List<Phone> phoned) {
      Internal.checkElementsNotNull(phoned);
      this.phoned = phoned;
      return this;
    }

    @Override
    public Data build() {
      if (user == null) {
        throw Internal.missingRequiredFields(user, "user");
      }
      return new Data(user, persons, phoned, super.buildUnknownFields());
    }
  }

  private static final class ProtoAdapter_Data extends ProtoAdapter<Data> {
    ProtoAdapter_Data() {
      super(FieldEncoding.LENGTH_DELIMITED, Data.class);
    }

    @Override
    public int encodedSize(Data value) {
      return ProtoAdapter.STRING.encodedSizeWithTag(1, value.user)
          + Person.ADAPTER.asRepeated().encodedSizeWithTag(2, value.persons)
          + Phone.ADAPTER.asRepeated().encodedSizeWithTag(3, value.phoned)
          + value.unknownFields().size();
    }

    @Override
    public void encode(ProtoWriter writer, Data value) throws IOException {
      ProtoAdapter.STRING.encodeWithTag(writer, 1, value.user);
      Person.ADAPTER.asRepeated().encodeWithTag(writer, 2, value.persons);
      Phone.ADAPTER.asRepeated().encodeWithTag(writer, 3, value.phoned);
      writer.writeBytes(value.unknownFields());
    }

    @Override
    public Data decode(ProtoReader reader) throws IOException {
      Builder builder = new Builder();
      long token = reader.beginMessage();
      for (int tag; (tag = reader.nextTag()) != -1;) {
        switch (tag) {
          case 1: builder.user(ProtoAdapter.STRING.decode(reader)); break;
          case 2: builder.persons.add(Person.ADAPTER.decode(reader)); break;
          case 3: builder.phoned.add(Phone.ADAPTER.decode(reader)); break;
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
    public Data redact(Data value) {
      Builder builder = value.newBuilder();
      Internal.redactElements(builder.persons, Person.ADAPTER);
      Internal.redactElements(builder.phoned, Phone.ADAPTER);
      builder.clearUnknownFields();
      return builder.build();
    }
  }
}
