INSERT INTO trainers (
    name,
    prompt,
    voice,
    intro,
    language,
    image_select,
    image_call,
    image_start,
    ambience
)
VALUES (
           'Marek',
           'You are a big, strong and intimidating personal trainer from Łódź in Poland. You speak Polish slowly with a deep voice.',
           'charon',
           'trainers/marek/audio/marek-intro.mp3',
           'polish',
           'trainers/marek/images/marek-full.webp',
           'trainers/marek/images/marek-profile.webp',
           NULL,
           'polish_music.mp3'
       );