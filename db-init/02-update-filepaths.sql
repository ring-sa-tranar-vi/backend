UPDATE trainers AS t
SET
    intro = v.new_intro,
    image_call = v.new_image_call,
    image_select = v.new_image_select
FROM (
    VALUES
        (1, 'trainers/eva/audio/eva-intro.mp3', 'trainers/eva/images/eva-profile.png', 'trainers/eva/images/eva-full.png'),
        (2, 'trainers/lunken/audio/lunken-intro.mp3', 'trainers/lunken/images/lunken-profile.png', 'trainers/lunken/images/lunken-full.png'),
        (3, 'trainers/jerry/audio/jerry-intro.mp3', 'trainers/jerry/images/jerry-profile.png', 'trainers/jerry/images/jerry-full.png'),
        (4, 'trainers/elizabeth/audio/elizabeth-intro.mp3', 'trainers/elizabeth/images/elizabeth-profile.png', 'trainers/elizabeth/images/elizabeth-full.png'),
        (6, 'trainers/ayesha/audio/ayesha-intro.mp3', 'trainers/ayesha/images/ayesha-profile.webp', 'trainers/ayesha/images/ayesha-full.webp'),
        (7, 'trainers/arjun/audio/arjun-intro.mp3', 'trainers/arjun/images/arjun-profile.webp', 'trainers/arjun/images/arjun-full.webp'),
        (8, 'trainers/axmed/audio/axmed-intro.mp3', 'trainers/axmed/images/axmed-profile.webp', 'trainers/axmed/images/axmed-full.webp')
) AS v(id, new_intro, new_image_call, new_image_select)
WHERE t.id = v.id;

UPDATE workouts AS w
SET
    instructions_audio = v.new_instructions_audio,
    instructions_video = v.new_instructions_video,
    workout_audio = v.new_workout_audio,
    workout_video = v.new_workout_video
FROM (
    VALUES
        (1, 'trainers/eva/audio/eva-shoulder-shrugs-instructions.mp3', 'trainers/eva/video/eva-shoulder-shrugs.mp4', 'trainers/eva/audio/eva-shoulder-shrugs-workout.mp3', 'trainers/eva/video/eva-shoulder-shrugs.mp4'),
        (2, 'trainers/lunken/audio/lunken-shoulder-shrugs-instructions.mp3', 'trainers/lunken/video/lunken-shoulder-shrugs.mp4', 'trainers/lunken/audio/lunken-shoulder-shrugs-workout.mp3', 'trainers/lunken/video/lunken-shoulder-shrugs.mp4'),
        (3, 'trainers/jerry/audio/jerry-shoulder-shrugs-instructions.mp3', null, 'trainers/jerry/audio/jerry-shoulder-shrugs-workout.mp3', null),
        (4, 'trainers/elizabeth/audio/elizabeth-shoulder-shrugs-instructions.mp3', null, 'trainers/elizabeth/audio/elizabeth-shoulder-shrugs-workout.mp3', null),
        (6, 'trainers/ayesha/ayesha-shoulder-shrugs-instructions.mp3', 'trainers/ayesha/video/ayesha-shoulder-shrugs.mp4', 'trainers/ayesha/audio/ayesha-shoulder-shrugs-workout.mp3', 'trainers/ayesha/video/ayesha-shoulder-shrugs.mp4')
) AS v(trainer_id, new_instructions_audio, new_instructions_video, new_workout_audio, new_workout_video)
WHERE w.trainer_id = v.trainer_id;