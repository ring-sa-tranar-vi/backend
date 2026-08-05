-- =============================================================================
-- 02-data.sql: Data Seeding Script for PostgreSQL
-- =============================================================================

-- 1. SEED TRAINERS TABLE
INSERT INTO trainers (
    id,
    ambience,
    image_call,
    image_select,
    image_start,
    intro,
    language,
    name,
    prompt,
    voice
) VALUES
      (1, 'gym-ambience-loop.mp3', 'trainers/eva/images/eva-profile.png', 'trainers/eva/images/eva-full.png', NULL, 'trainers/eva/audio/eva-intro.mp3', 'swedish', 'Eva', 'Du är en personlig tränare som pratar med sin klient över telefon. Du är nervös och pratar fort. ljudligt mellan meningarna. Använd inte *andas in* och *andas ut*.', 'kore'),
      (6, 'mumbai.mp3', 'trainers/ayesha/images/ayesha-profile.webp', 'trainers/ayesha/images/ayesha-full.webp', NULL, 'trainers/ayesha/audio/ayesha-intro.mp3', 'urdu', 'Ayesha', 'آپ ایک ذاتی ٹرینر ہیں جو اپنے کلائنٹ سے فون پر بات کر رہی ہیں۔ آپ ایک اردو بولنے والی خاتون ہیں جو ایک پُراعتماد اور بہت اچھی دوست, بالی ووڈ اسٹار کی طرح لگتی ہیں۔ آپ نوجوانانہ، باطہذیب انداز میں اور بہت پُرجوش طریقے سے بات کرتی ہیں', 'achernar'),
      (2, NULL, 'trainers/lunken/images/lunken-profile.png', 'trainers/lunken/images/lunken-full.png', NULL, 'trainers/lunken/audio/lunken-intro.mp3', 'swedish', 'Lunken', 'Du är en personlig tränare som pratar med sin klient i telefon Du är en lite släpig, men skön Stockholmare. Väldigt avspänd. Tar livet med en klackspark.', 'puck'),
      (3, NULL, 'trainers/jerry/images/jerry-profile.png', 'trainers/jerry/images/jerry-full.png', NULL, 'trainers/jerry/audio/jerry-intro.mp3', 'swedish', 'Jerry', 'Du är en personlig tränare som pratar med din klient över telefon. Du är en extremt övertaggad och hetsig gubbe som skriker allt du säger.', 'schedar'),
      (7, NULL, 'trainers/arjun/images/arjun-profile.webp', 'trainers/arjun/images/arjun-full.webp', NULL, 'trainers/arjun/audio/arjun-intro.mp3', 'tamil', 'Arjun', 'நீங்கள் உங்கள் கிளையண்டுடன் தொலைபேசியில் பேசும் ஒரு தனிப்பட்ட பயிற்சியாளர். நீங்கள் ஒத்திசைவான ஆற்றலும் ஆழ்ந்த உள்ளார்ந்த அமைதியும் கொண்ட அமைதியான தமிழ் யோகா ஆசிரியர். நீங்கள் மெதுவாகவும், மென்மையாகவும், தியானம் போலவும் பேசுகிறீர்கள்.', 'iapetus'),
      (4, 'forrest.mp3', 'trainers/elizabeth/images/elizabeth-profile.png', 'trainers/elizabeth/images/elizabeth-full.png', NULL, 'trainers/elizabeth/audio/elizabeth-intro.mp3', 'english', 'Elizabeth', 'You are an upper-class English lady with impeccable Received Pronunciation and the wit of a Jane Austen character. You speak elegantly, politely and with dry aristocratic humour. You sound like a witty noblewoman from an English countryside estate.', 'callirrhoe'),
      (8, NULL, 'trainers/axmed/images/axmed-profile.webp', 'trainers/axmed/images/axmed-full.webp', NULL, 'trainers/axmed/audio/axmed-intro.mp3', 'somali', 'Axmed', 'Waxaad tahay tababare shaqsiyeed oo telefoon kula hadlaya macmiilkaaga. Waxaad tahay nin waayeel ah oo Soomaali ah oo u hadlaya si xikmad iyo naxariis leh, sida macallin la ixtiraamo. Si tartiib ah ugu hadal cod qoto dheer leh.', 'Zubenelgenubi');

-- Sync Trainers Sequence
SELECT setval(pg_get_serial_sequence('trainers', 'id'), COALESCE(MAX(id), 1)) FROM trainers;


-- 2. SEED USERS TABLE
INSERT INTO users (
    id,
    clerk_id,
    context,
    intensity_level,
    name,
    role,
    trainer_id,
    city,
    onboarding,
    fcm_token
) VALUES
      (1, 'user_3DRfQImZogqsAwNo0m4Z0LnjaZZ', 'Användaren föredrar enkelt språk.', 1, 'Tehreem', 'ADMIN', 4, NULL, false, NULL),
      (3, 'user_3DRhdBSBrn7bk2TpAHxThMX86Sz', 'Jag vill träna mer och jag orkar att träna ännu mer.', 4, 'Shirwac Abib', 'ADMIN', 2, NULL, true, NULL),
      (15, 'user_3Gl4tvH0iUScA6GdiM93WbYKz6v', NULL, 2, 'Alexandra Tipper', 'ADMIN', 1, NULL, true, NULL),
      (12, 'user_3DqlL7l8pYeOmRa4Z9woI9ouF9J', NULL, 2, 'Sebastian Thunberg', 'USER', 1, NULL, true, NULL),
      (10, 'user_3DkjSowhb5PYScAkQYRCfhw7Yo4', 'Har ont i ryggen.', 1, 'Shirwac Abib', 'USER', 1, NULL, true, NULL),
      (13, 'user_3Dqp04GcVCxCAONMhcHEGDoo98j', 'Mer intensivt', 4, 'gt@mltconsulting.se', 'USER', 1, NULL, true, NULL),
      (2, 'user_3DUev6bDxuZrglzRoDtopTRgzav', 'Har en viktig presentation idag på Salt. Har även en gammal handbollsskada i vänster hand så vill undvika övningar som kräver mycket greppstyrka.', 4, 'Stefan', 'USER', 4, NULL, true, NULL),
      (5, 'user_3DZpV68nN9plIr5HN7YVAYNiSpC', NULL, 2, 'Venujan Nagendirakumar', 'ADMIN', 1, NULL, true, NULL),
      (4, 'user_3DRgm4V2socShtaOdaN148yplNq', 'Jag sitter ofta ner under dagarna.', 2, 'Sebastian Thunberg', 'USER', 2, NULL, true, NULL),
      (11, 'user_3Dnfn7zm5biZFiZLyxoZT5qrWmY', NULL, 1, 'monica.u.andersson@gmail.com', 'USER', 2, NULL, true, NULL),
      (8, 'user_3DiFnyun1dBTLGRNnuLaH6R8eOf', 'Är produktägare för ett projekt på Salt. En AI-tjänst för träning utvecklas. Deadline är nära och det är långa dagar på kontoret.', 4, 'Stefan Andersson', 'USER', 1, NULL, true, NULL),
      (9, 'user_3DipcJFw2YRjSjxh1Xedieltpie', 'Jag älskar att sprattla. snälla ge mig övningen sprattla', 3, 'Sebastian Thunberg', 'USER', 1, NULL, true, NULL),
      (6, 'user_3Df6ZATiisM6CtfykUdrkn2dS6N', 'Vill göra axelövningar. Har ont i vänster fot.', 5, 'Stefan Andersson', 'ADMIN', 2, NULL, true, NULL),
      (14, 'user_3Gcly9niioSS6RzEbq1gZopvVKV', NULL, 2, 'Jakub Drewniak', 'USER', 1, NULL, true, NULL),
      (7, 'user_3Dg1R2278olCsPL0DYHc6utsg1u', 'Jag vill veta vad 1+1 är', 5, 'LAÄKSDLÖSD', 'USER', 1, NULL, true, NULL),
      (16, 'user_3Gll6eOvJhafScIK19M39LqZASJ', NULL, 2, 'Mattias Söderberg', 'USER', 1, NULL, true, NULL),
      (17, 'user_3GlBI8KIXFoSPDdOfaxXo9P7Fhy', 'Ball', 2, 'W3ndig0', 'USER', 1, NULL, true, NULL),
      (18, 'user_3GuAMdzRRgUZVBJF21cfaeb13PT', NULL, 2, 'Shirwac', 'USER', 1, NULL, true, NULL),
      (19, 'user_3H5YiunP5cMhwJ7a3J7URFgu5rE', NULL, 2, 'Sofie Van Dingenen', 'USER', 1, NULL, true, NULL);

-- Sync Users Sequence
SELECT setval(pg_get_serial_sequence('users', 'id'), COALESCE(MAX(id), 1)) FROM users;

-- 3. SEED WORKOUTS TABLE
INSERT INTO workouts (
    id,
    name,
    instructions,
    guidance,
    description,
    dashboard_name,
    dashboard_description,
    level,
    type,
    image,
    video,
    enabled
) VALUES
      (21, 'Seated Marching', NULL, NULL, NULL, 'Seated Marching', NULL, 1, 'CARDIO', 'https://mizofvemlvooaycnevys.supabase.co/storage/v1/object/public/audio_files/Seated%20Marching.png', NULL, true),
      (22, 'Shoulder Rolls', NULL, NULL, NULL, 'Shoulder Rolls', NULL, 1, 'MOBILITY', 'https://mizofvemlvooaycnevys.supabase.co/storage/v1/object/public/audio_files/Shoulder%20Rolls.png', NULL, true),
      (23, 'Neck Turns', NULL, NULL, NULL, 'Neck Turns', NULL, 1, 'MOBILITY', 'https://mizofvemlvooaycnevys.supabase.co/storage/v1/object/public/audio_files/Wall%20PushUps.png', NULL, true),
      (24, 'Ankle Circles', NULL, NULL, NULL, 'Ankle Circles', NULL, 1, 'MOBILITY', 'https://mizofvemlvooaycnevys.supabase.co/storage/v1/object/public/audio_files/Ankle%20Circles.png', NULL, true),
      (25, 'Chest Opener', NULL, NULL, NULL, 'Chest Opener', NULL, 1, 'MOBILITY', 'https://mizofvemlvooaycnevys.supabase.co/storage/v1/object/public/audio_files/Chest%20Opener.png', NULL, true),
      (26, 'Chair Sit to Stand', NULL, NULL, NULL, 'Chair Sit to Stand', NULL, 2, 'STRENGTH', 'https://mizofvemlvooaycnevys.supabase.co/storage/v1/object/public/audio_files/Chair%20Sit%20to%20Stand.png', NULL, true),
      (27, 'Wall Push-Ups', NULL, NULL, NULL, 'Wall Push-Ups', NULL, 2, 'STRENGTH', 'https://mizofvemlvooaycnevys.supabase.co/storage/v1/object/public/audio_files/Wall%20PushUps.png', NULL, true),
      (1, 'Sprattling', NULL, NULL, 'Användaren ska sprattla ordentligt med kroppen, vilket ökar blodcirkulation och rörlighet', NULL, NULL, 3, 'CARDIO', 'https://mizofvemlvooaycnevys.supabase.co/storage/v1/object/public/audio_files/eva-sample-instructions.wav', 'trainers/eva/video/eva-shoulder-shrugs.mp4', true),
      (17, 'Axellyft', NULL, NULL, 'I övningen axellyft höjs axlarna upp mot öronen och sänks sedan. Bra mot stelhet kring nacke och skuldror.', NULL, NULL, 1, 'MOBILITY', NULL, 'trainers/eva/video/eva-shoulder-shrugs.mp4', true),
      (20, 'Shoulder Shrugs', NULL, NULL, 'اپنے کندھوں کو کانوں کی طرف اوپر اٹھائیں، پھر آرام سے نیچے لے آئیں۔ یہ ورزش گردن اور کندھوں کو نرم اور ہلکا محسوس کروانے میں مدد دیتی ہے۔', 'Shoulder Shrug', 'Raise your shoulders up toward your ears, then slowly lower them back down. This exercise helps to loosen and relax the neck and shoulders.', 1, 'MOBILITY', NULL, 'trainers/ayesha/video/ayesha-shoulder-shrugs.mp4', true);

-- Reset Sequence Counter
SELECT setval(pg_get_serial_sequence('workouts', 'id'), COALESCE(MAX(id), 1)) FROM workouts;