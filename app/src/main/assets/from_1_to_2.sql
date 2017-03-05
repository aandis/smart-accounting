-- This migration fixes floating point error introduced in first release.
-- The goal is to change the column type of amount column to integer
-- from real and transform values stored from real to integer.
-- Since sqlite doesn't support column type change the procedure followed is as follows -
-- 1. Rename old table.
-- 2. Create a new table with the table's original name.
-- 3. Move all data from old table to new table with transformed values.
-- 4. Drop old table.
-- 5. Recreate any dropped INDEXES, TRIGGERS on the new table etc.

-- Fix purchase items table.
ALTER TABLE purchase_items RENAME TO purchase_items_tmp
;

CREATE TABLE purchase_items (
    _id         integer primary key autoincrement,
    p_id        integer not null,
    name        text not null,
    quantity    integer not null,
    rate        integer not null,
    amount      integer not null,
    foreign key (p_id) REFERENCES purchases (_id) ON DELETE CASCADE
)
;

INSERT INTO purchase_items (_id, p_id, name, quantity, rate, amount)
     SELECT _id, p_id, name,
            cast(quantity * 100 as INTEGER),
            cast(rate * 100 as INTEGER),
            cast(amount * 100 as INTEGER)
       FROM purchase_items_tmp
;


DROP TABLE purchase_items_tmp
;

CREATE TRIGGER update_purchase_amount
AFTER UPDATE OF quantity, rate
             ON purchase_items
   FOR EACH ROW
           BEGIN
            UPDATE purchase_items
               SET amount = cast(rate*quantity/100 as INTEGER)
             WHERE _id = OLD._id;
           END
;


-- Fix credits table.
ALTER TABLE credits RENAME TO credits_tmp
;

CREATE TABLE credits (
        _id integer primary key autoincrement,
       c_id integer not null,
     amount integer not null,
       date text not null,
    remarks text,
       type text NOT NULL CHECK (type IN ('credit','debit')),
    foreign key (c_id) REFERENCES customers(_id) ON DELETE CASCADE
)
;

INSERT INTO credits (_id, c_id, amount, date, remarks, type)
     SELECT _id, c_id, cast(amount * 100 as INTEGER), date, remarks, type
       FROM credits_tmp
;

DROP TABLE credits_tmp
;
