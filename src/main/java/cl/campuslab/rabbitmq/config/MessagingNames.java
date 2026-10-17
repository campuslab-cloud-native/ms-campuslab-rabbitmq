package cl.campuslab.rabbitmq.config;

public final class MessagingNames {

    public static final String DIRECT_EXCHANGE = "cmd.direct";
    public static final String TOPIC_EXCHANGE = "cmd.topic";
    public static final String DEAD_LETTER_EXCHANGE = "cmd.dead.dlx";

    public static final String QUEUE_EMAIL = "q.cmd.email";
    public static final String QUEUE_PREP = "q.cmd.prep";
    public static final String QUEUE_VOUCHER = "q.cmd.voucher";

    public static final String QUEUE_EMAIL_DLQ = "q.cmd.email.dlq";
    public static final String QUEUE_PREP_DLQ = "q.cmd.prep.dlq";
    public static final String QUEUE_VOUCHER_DLQ = "q.cmd.voucher.dlq";

    public static final String ROUTING_KEY_EMAIL_DIRECT = "email.send";
    public static final String ROUTING_KEY_EMAIL_TOPIC = "email.*";
    public static final String ROUTING_KEY_PREP_DIRECT = "prep.ticket";
    public static final String ROUTING_KEY_PREP_TOPIC = "prep.#";
    public static final String ROUTING_KEY_VOUCHER_DIRECT = "voucher.gen";
    public static final String ROUTING_KEY_VOUCHER_TOPIC = "voucher.*";

    public static final String DLQ_ROUTING_KEY_EMAIL = "dead.email";
    public static final String DLQ_ROUTING_KEY_PREP = "dead.prep";
    public static final String DLQ_ROUTING_KEY_VOUCHER = "dead.voucher";

    public static final String EXCHANGE_TYPE_DIRECT = "direct";
    public static final String EXCHANGE_TYPE_TOPIC = "topic";
    public static final String EXCHANGE_TYPE_FANOUT = "fanout";

    private MessagingNames() {
        throw new IllegalStateException("Utility class");
    }
}
