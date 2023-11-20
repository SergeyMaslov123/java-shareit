create TABLE IF NOT EXISTS users (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(512) NOT NULL,
  CONSTRAINT pk_user PRIMARY KEY (id),
  CONSTRAINT UQ_USER_EMAIL UNIQUE(email)
);

create TABLE IF NOT EXISTS requests (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  description VARCHAR(255),
  requestor BIGINT,
  created timestamp NOT NULL,
  CONSTRAINT pk_request PRIMARY KEY (id),
  CONSTRAINT fk_requset_to_user FOREIGN KEY(requestor) REFERENCES users(id)
);

create TABLE IF NOT EXISTS items (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(255) NOT NULL,
  available BOOLEAN,
  owner BIGINT NOT NULL,
  request BIGINT,
  CONSTRAINT pk_item PRIMARY KEY (id),
  CONSTRAINT fk_item_to_user FOREIGN KEY(owner) REFERENCES users(id),
  CONSTRAINT fk_item_to_request FOREIGN KEY(request) REFERENCES requests(id)
);
create TABLE IF NOT EXISTS bookings (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  start_date timestamp NOT NULL,
  end_date timestamp NOT NULL,
  item BIGINT,
  booker BIGINT,
  status VARCHAR(50),
  CONSTRAINT pk_booking PRIMARY KEY (id),
  CONSTRAINT fk_booking_to_item FOREIGN KEY(item) REFERENCES items(id),
  CONSTRAINT fk_booking_to_user FOREIGN KEY(booker) REFERENCES users(id)
);
create TABLE IF NOT EXISTS comments(
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  text VARCHAR(255),
  item BIGINT,
  author BIGINT,
  created timestamp,
  CONSTRAINT pk_comments PRIMARY KEY (id),
  CONSTRAINT fk_comments_to_item FOREIGN KEY(item) REFERENCES items(id),
  CONSTRAINT fk_comments_to_user FOREIGN KEY(author) REFERENCES users(id)
);
