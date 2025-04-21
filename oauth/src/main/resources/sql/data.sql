-- Insert admin user with encoded password (password is 'admin')
INSERT INTO users (username, password, email, enabled)
VALUES ('admin', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW', 'admin@custos.com', true);

-- Insert admin authority
INSERT INTO authorities (username, authority)
VALUES ('admin', 'ROLE_ADMIN'); 