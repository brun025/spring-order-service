package com.order.demo.infrastructure.persistence.types;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import com.order.demo.domain.enums.OrderStatus;

public class PostgreSQLEnumType implements UserType<OrderStatus> {

    @Override
    public int getSqlType() {
        return Types.OTHER;
    }

    @Override
    public Class<OrderStatus> returnedClass() {
        return OrderStatus.class;
    }

    @Override
    public boolean equals(OrderStatus x, OrderStatus y) {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(OrderStatus x) {
        return x.hashCode();
    }

    @Override
    public OrderStatus nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) 
    throws SQLException {
        String name = rs.getString(position);
        if (rs.wasNull()) {
            return null;
        }
        return OrderStatus.valueOf(name);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, OrderStatus value, int index, SharedSessionContractImplementor session) 
    throws SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
        } else {
            st.setObject(index, value.name(), Types.OTHER);
        }
    }

    @Override
    public OrderStatus deepCopy(OrderStatus value) {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(OrderStatus value) {
        return value;
    }

    @Override
    public OrderStatus assemble(Serializable cached, Object owner) {
        return (OrderStatus) cached;
    }
}