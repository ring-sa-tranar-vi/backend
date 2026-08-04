ALTER TABLE workouts
    ADD COLUMN workout_guidance VARCHAR(2000);

ALTER TABLE workouts
    ADD COLUMN workout_instructions VARCHAR(2000);

UPDATE workouts
SET workout_instructions = 'Användaren ska stå eller sitta med rak rygg. Armarna ska vara avslappnade längs sidorna. Axlarna ska först höjas upp mot öronen och sedan sänkas ner mot marken. Viktigt att axlarna rör sig rakt upp och rakt ner och inte faller framåt. Vi gör 5 repetitioner.'
WHERE id IN (16, 17, 18, 19, 20, 28);

UPDATE workouts
SET workout_guidance = 'Guida användaren att höja axlarna och sedan sänka ner dem. Prata långsamt och med lite mellanrum. Säg till användaren när den ska höja axlarna och när den ska sänka axlarna. Totalt 5 repetitioner. De 3 först görst i vanlig takt. De två sista har en paus när axlarna är helt uppe och du ska be användaren försöka höja axlarna ännu en liten bit. Säg något uppmuntrande inför sista repetitionen.'
WHERE id IN (16, 17, 18, 19, 20, 28);
