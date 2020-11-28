-- Add a created_at to purchases and credits tables.
-- sqlite doesn't allow adding a new column with
-- default timestamp so we follow
-- https://stackoverflow.com/a/25917323

-- purchases table
ALTER TABLE purchases ADD COLUMN created_at TEXT DEFAULT NULL /* replace me */
;
UPDATE purchases SET created_at = datetime(date)
;

-- credits table
ALTER TABLE credits ADD COLUMN created_at TEXT DEFAULT NULL /* replace me */
;
UPDATE credits SET created_at = datetime(date)
;

-- add created_at trigger to purchases.
CREATE TRIGGER add_created_at_to_purchases
AFTER INSERT ON purchases
FOR EACH ROW
WHEN NEW.created_at IS NULL
BEGIN
    UPDATE purchases
    SET created_at = datetime('now','localtime')
    WHERE _id = NEW._id;
END
;

-- add created_at trigger to credits.
CREATE TRIGGER add_created_at_to_credits
AFTER INSERT ON credits
FOR EACH ROW
WHEN NEW.created_at IS NULL
BEGIN
    UPDATE credits
    SET created_at = datetime('now','localtime')
    WHERE _id = NEW._id;
END
;
