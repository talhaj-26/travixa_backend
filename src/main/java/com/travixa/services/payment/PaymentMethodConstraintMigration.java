package com.travixa.services.payment;

import com.travixa.enums.PaymentMethod;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.stream.Collectors;

/** Keeps the existing PostgreSQL method check aligned with the application enum. */
@Component
public class PaymentMethodConstraintMigration implements ApplicationRunner {

    private static final String CONSTRAINT_NAME = "payments_method_check";
    private final JdbcTemplate jdbcTemplate;

    public PaymentMethodConstraintMigration(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Boolean tableExists = jdbcTemplate.queryForObject(
                "select to_regclass(current_schema() || '.payments') is not null", Boolean.class);
        if (!Boolean.TRUE.equals(tableExists)) {
            return;
        }

        jdbcTemplate.execute("select pg_advisory_xact_lock(hashtext('travixa:payments_method_check'))");

        String definition = jdbcTemplate.query(
                "select pg_get_constraintdef(oid) from pg_constraint "
                        + "where conrelid = to_regclass(current_schema() || '.payments') and conname = ?",
                statement -> statement.setString(1, CONSTRAINT_NAME),
                result -> result.next() ? result.getString(1) : null);

        if (definition != null && definition.contains("'DEMO'")) {
            return;
        }

        String allowedMethods = Arrays.stream(PaymentMethod.values())
                .map(method -> "'" + method.name() + "'")
                .collect(Collectors.joining(", "));
        if (definition != null) {
            jdbcTemplate.execute("alter table payments drop constraint " + CONSTRAINT_NAME);
        }
        jdbcTemplate.execute("alter table payments add constraint " + CONSTRAINT_NAME
                + " check (method in (" + allowedMethods + "))");
    }
}
