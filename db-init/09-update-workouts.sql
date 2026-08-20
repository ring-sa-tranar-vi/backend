BEGIN TRANSACTION;

-- ID 1: Body Flailing
UPDATE workouts
SET name = 'Body Flailing',
    description = 'The user should flail their body vigorously, which increases blood circulation and mobility.',
    instructions = 'Stand or sit comfortably with space around you. Vigorously shake and flail your arms, legs, and torso to get your blood circulating. Continue for 30 seconds.',
    guidance = '### Style & Persona Guidelines
1. **Word Choice:** Keep prompts energetic, fun, and liberating.
2. **Clarity First:** Focus on loose, energetic movement rather than strict technique.

### Pacing & Timing Rules (NON-NEGOTIABLE)
1. **Cadence:** High energy and continuous delivery.
2. **Structural Pauses:** Include specified pauses (`...` or `[pause Xs]`) between phases.

---

### Movement Execution Flow

* **Start Phase:** Begin shaking out your hands and feet... [pause 1s]
* **Full Body Phase:** Let the movement shake through your arms, legs, and torso... [pause 2s]
* **Peak Phase:** Flail vigorously and release all built-up tension... [pause 2s]
* **Cooldown Phase:** Gradually slow down the shaking and bring your body back to a calm standstill.'
WHERE id = 1;

-- ID 17: Shoulder Lifts
UPDATE workouts
SET name = 'Shoulder Lifts',
    description = 'In the shoulder lift exercise, the shoulders are raised toward the ears and then lowered. Great against stiffness around the neck and shoulders.',
    instructions = 'Stand or sit with a straight back. Raise your shoulders up toward your ears in a controlled manner, then lower them smoothly back down. We do 5 repetitions.',
    guidance = '### Style & Persona Guidelines
1. **Word Choice:** Use calm, fluid phrasing (e.g., "Raise your shoulders", "Gently lower", "Release tension").
2. **Clarity First:** Keep commands clear and focused on releasing tension around the neck and shoulders.

### Pacing & Timing Rules (NON-NEGOTIABLE)
1. **Cadence:** Speak slowly with distinct gaps.
2. **Structural Pauses:** Include specified pauses (`...` or `[pause Xs]`) between phases.

---

### Movement Execution Flow (5 Reps Total)

#### [Rep 1 — Standard Pace]
- Elevation Phase: Cue raising shoulders up... [pause 1s]
- Depression Phase: Cue lowering shoulders smoothly... [pause 1s]

#### [Rep 2 — Standard Pace]
- Elevation Phase: Cue raising shoulders up... [pause 1s]
- Depression Phase: Cue lowering shoulders smoothly... [pause 1s]

#### [Rep 3 — Standard Pace]
- Elevation Phase: Cue raising shoulders up... [pause 1s]
- Depression Phase: Cue lowering shoulders smoothly... [pause 1s]

#### [Rep 4 — Extra Lift Peak]
- Elevation Phase: Cue raising shoulders up... Hold at top... "Now try lifting a tiny bit higher..." [pause 1.5s]
- Depression Phase: Cue lowering shoulders back down... [pause 1s]

#### [Rep 5 — Extra Lift Peak]
- Motivation Cue: Offer an encouraging remark before this final rep.
- Elevation Phase: Cue raising shoulders up... Hold at top... "Now try lifting a tiny bit higher..." [pause 1.5s]
- Depression Phase: Cue lowering shoulders back down... [pause 1s]'
WHERE id = 17;

-- ID 20: Shoulder Shrugs
UPDATE workouts
SET description = 'Raise your shoulders up toward your ears, then gently lower them back down. This exercise helps the neck and shoulders feel soft and light.',
    instructions = 'Sit or stand upright. Slowly draw your shoulders up toward your ears, hold briefly, then let them drop back down comfortably. We do 6 repetitions.',
    guidance = '### Style & Persona Guidelines
1. **Word Choice:** Soft, soothing phrasing (e.g., "Shrug up", "Melt down", "Release").
2. **Clarity First:** Maintain a calm coaching tone.

### Pacing & Timing Rules (NON-NEGOTIABLE)
1. **Cadence:** Unhurried, relaxed delivery.
2. **Structural Pauses:** Include specified pauses (`...` or `[pause Xs]`) between phases.

---

### Movement Execution Flow (6 Reps Total)

#### [Rep 1 — Standard Pace]
- Lift Phase: Cue shrugging shoulders up toward ears... [pause 1s]
- Release Phase: Cue gently lowering shoulders back down... [pause 1s]

#### [Rep 2 — Standard Pace]
- Lift Phase: Cue shrugging shoulders up toward ears... [pause 1s]
- Release Phase: Cue gently lowering shoulders back down... [pause 1s]

#### [Rep 3 — Standard Pace]
- Lift Phase: Cue shrugging shoulders up toward ears... [pause 1s]
- Release Phase: Cue gently lowering shoulders back down... [pause 1s]

#### [Rep 4 — Standard Pace]
- Lift Phase: Cue shrugging shoulders up toward ears... [pause 1s]
- Release Phase: Cue gently lowering shoulders back down... [pause 1s]

#### [Rep 5 — Deep Stretch]
- Lift Phase: Cue shrugging shoulders up high... [pause 1.5s]
- Release Phase: Cue dropping shoulders completely, releasing all neck tension... [pause 2s]

#### [Rep 6 — Deep Stretch]
- Motivation Cue: Deliver a calming motivational word before starting this final rep.
- Lift Phase: Cue shrugging shoulders up high... [pause 1.5s]
- Release Phase: Cue dropping shoulders completely, releasing all neck tension... [pause 2s]'
WHERE id = 20;

-- ID 21: Seated Marching
UPDATE workouts
SET description = 'Gentle seated cardio exercise that activates hips and legs.',
    instructions = 'Sit near the front of a sturdy chair with a straight back and feet flat on the floor. Alternately lift one knee a few centimeters off the floor and place the foot back down. One right and left lift counts as one repetition. We do 6 repetitions.',
    guidance = '### Style & Persona Guidelines
Form Reminder: Keep reminding the user to maintain a tall, straight posture.

Pacing & Timing Rules:
1. Cadence: Smooth, unhurried pace.

Movement Execution Flow (6 Reps Total):

[Rep 1 — Regular Tempo]
- Right Leg: Cue lifting right knee... [pause 1s] ...Lower down.
- Left Leg: Cue lifting left knee... [pause 1s] ...Lower down.

[Rep 2 — Regular Tempo]
- Right Leg: Cue lifting right knee... [pause 1s] ...Lower down.
- Left Leg: Cue lifting left knee... [pause 1s] ...Lower down.

[Rep 3 — Regular Tempo]
- Right Leg: Cue lifting right knee... [pause 1s] ...Lower down.
- Left Leg: Cue lifting left knee... [pause 1s] ...Lower down.

[Rep 4 — Regular Tempo]
- Right Leg: Cue lifting right knee... [pause 1s] ...Lower down.
- Left Leg: Cue lifting left knee... [pause 1s] ...Lower down.

[Rep 5 — Short Isometric Hold]
- Right Leg: Cue lifting right knee... "Hold it there briefly..." [pause 1.5s] ...Lower.
- Left Leg: Cue lifting left knee... "Hold briefly..." [pause 1.5s] ...Lower.

[Rep 6 — Short Isometric Hold]
- Motivation Cue: Share an encouraging thought before this final rep.
- Right Leg: Cue lifting right knee... "Hold it there briefly..." [pause 1.5s] ...Lower.
- Left Leg: Cue lifting left knee... "Hold briefly..." [pause 1.5s] ...Lower.'
WHERE id = 21;

-- ID 22: Shoulder Rolls
UPDATE workouts
SET description = 'Simple mobility movement to reduce upper back and shoulder tightness.',
    instructions = 'Sit or stand tall with arms relaxed. Roll your shoulders in smooth, continuous circles—up toward your ears, back, and down. Perform 6 reps backward and 6 reps forward.',
    guidance = '### Style & Persona Guidelines
1. **Word Choice:** Use circular, fluid cues (e.g., "Trace smooth circles", "Open the chest").

### Pacing & Timing Rules (NON-NEGOTIABLE)
1. **Cadence:** Continuous and fluid pacing.

---

### Movement Execution Flow (6 Backward Rolls, 6 Forward Rolls)

#### [Backward Rotations — 6 Reps]
- Circle Phase: Cue rolling shoulders up, backward, and down in a large circle... [pause 1s per rotation]

#### [Forward Rotations — 6 Reps]
- Circle Phase: Switch directions... Cue rolling shoulders up, forward, and down... [pause 1s per rotation]'
WHERE id = 22;

-- ID 23: Neck Turns
UPDATE workouts
SET description = 'Gentle neck mobility exercise to improve rotation and relieve stiffness.',
    instructions = 'Sit or stand tall with relaxed shoulders. Slowly turn your head to look over your right shoulder, return to center, then turn to look over your left shoulder. Perform 5 repetitions per side.',
    guidance = '### Style & Persona Guidelines
1. **Safety Cue:** Remind user to move strictly within a pain-free range without forcing the movement.

### Pacing & Timing Rules (NON-NEGOTIABLE)
1. **Cadence:** Very slow, deliberate delivery.

---

### Movement Execution Flow (5 Reps Total)

#### [Reps 1 to 5]
- Right Turn: Cue slowly turning head to look right... [pause 1.5s]
- Center Phase: Cue returning head to center... [pause 1s]
- Left Turn: Cue slowly turning head to look left... [pause 1.5s]
- Center Phase: Cue returning head to center... [pause 1s]'
WHERE id = 23;

-- ID 24: Ankle Circles
UPDATE workouts
SET description = 'Lower leg mobility exercise to improve ankle flexibility and foot circulation.',
    instructions = 'Sit comfortably in a chair. Lift one foot off the floor and rotate the ankle in full, smooth circles. Complete 5 rotations clockwise and 5 counter-clockwise, then switch legs.',
    guidance = '### Style & Persona Guidelines
1. **Word Choice:** Focus on full range of motion (e.g., "Draw big, full circles with your toes").

### Pacing & Timing Rules (NON-NEGOTIABLE)
1. **Cadence:** Smooth, continuous rotations.

---

### Movement Execution Flow (5 Rotations Each Way per Leg)

#### [Right Foot]
- Clockwise Phase: Cue drawing big circles clockwise... [pause 1s per rotation]
- Counter-Clockwise Phase: Cue reversing direction counter-clockwise... [pause 1s per rotation]

#### [Left Foot]
- Clockwise Phase: Cue drawing big circles clockwise... [pause 1s per rotation]
- Counter-Clockwise Phase: Cue reversing direction counter-clockwise... [pause 1s per rotation]'
WHERE id = 24;

-- ID 25: Chest Opener
UPDATE workouts
SET description = 'Mobility for chest and shoulders.',
    instructions = 'The user should sit or stand with a straight back. Arms hanging along sides with palms facing forward. Move arms slightly backward and draw shoulder blades gently together. Relax and return to starting position. We do 6 repetitions.',
    guidance = 'Style & Persona Guidelines:
Form Cue: Keep shoulders down away from ears; movement must remain pain-free.

Pacing & Timing Rules:
1. Cadence: Slow, expansive breathing rhythm.

Movement Execution Flow (6 Reps Total):

[Rep 1 — Regular Tempo]
- Open Phase: Cue opening chest and drawing shoulder blades gently together... [pause 1s]
- Release Phase: Cue releasing back to start... [pause 1s]

[Rep 2 — Regular Tempo]
- Open Phase: Cue opening chest and drawing shoulder blades gently together... [pause 1s]
- Release Phase: Cue releasing back to start... [pause 1s]

[Rep 3 — Regular Tempo]
- Open Phase: Cue opening chest and drawing shoulder blades gently together... [pause 1s]
- Release Phase: Cue releasing back to start... [pause 1s]

[Rep 4 — Regular Tempo]
- Open Phase: Cue opening chest and drawing shoulder blades gently together... [pause 1s]
- Release Phase: Cue releasing back to start... [pause 1s]

[Rep 5 — 2-Second Stretch]
- Open Phase: Cue opening chest... Hold the open stretch... Count: "One... Two..."
- Release Phase: Cue easing back to start... [pause 1s]

[Rep 6 — 2-Second Stretch]
- Motivation Cue: Inspire the user before this final rep.
- Open Phase: Cue opening chest... Hold the open stretch... Count: "One... Two..."
- Release Phase: Cue easing back to start... [pause 1s]'
WHERE id = 25;

-- ID 26: Chair Sit to Stand
UPDATE workouts
SET description = 'Functional strength exercise for legs and glutes.',
    instructions = 'The user should sit far forward on a sturdy chair with feet hip-width apart on the floor. Rise up to standing, then slowly sit back down. Knees should track in the same direction as toes. Armrests may be used if needed. We do 6 repetitions.',
    guidance = 'Style & Persona Guidelines:
Form Cues: Feet firmly planted; ensure knees do not cave inward.

Pacing & Timing Rules:
1. Cadence: Controlled hinge, powerful rise, slow eccentric drop.

Movement Execution Flow (6 Reps Total):

[Rep 1 — Regular Tempo]
- Stand Phase: Cue leaning slightly forward, driving through feet to stand tall... [pause 1s]
- Sit Phase: Cue slowly lowering back down to chair with control... [pause 1s]

[Rep 2 — Regular Tempo]
- Stand Phase: Cue leaning slightly forward, driving through feet to stand tall... [pause 1s]
- Sit Phase: Cue slowly lowering back down to chair with control... [pause 1s]

[Rep 3 — Regular Tempo]
- Stand Phase: Cue leaning slightly forward, driving through feet to stand tall... [pause 1s]
- Sit Phase: Cue slowly lowering back down to chair with control... [pause 1s]

[Rep 4 — Regular Tempo]
- Stand Phase: Cue leaning slightly forward, driving through feet to stand tall... [pause 1s]
- Sit Phase: Cue slowly lowering back down to chair with control... [pause 1s]

[Rep 5 — 2-Second Standing Hold]
- Stand Phase: Cue rising to full standing position... Hold still... Count: "One... Two..."
- Sit Phase: Cue braking movement on the way down to sit... [pause 1s]

[Rep 6 — 2-Second Standing Hold]
- Motivation Cue: Energize the user before doing this final rep.
- Stand Phase: Cue rising to full standing position... Hold still... Count: "One... Two..."
- Sit Phase: Cue braking movement on the way down to sit... [pause 1s]'
WHERE id = 26;

-- ID 27: Wall Push-Ups
UPDATE workouts
SET description = 'Light pressing exercise for chest, shoulders, and arms.',
    instructions = 'The user should stand facing a wall, roughly an arm-length away. Place hands against wall at shoulder height. Bend elbows to bring body toward wall without arching lower back. Press back to straight arms. We do 6 repetitions.',
    guidance = 'Style & Persona Guidelines:
Form Cue: Remind user to maintain a straight line from head to heels.

Pacing & Timing Rules:
1. Cadence: Slow descent, powerful press.

Movement Execution Flow (6 Reps Total):

[Rep 1 — Regular Tempo]
- Descent Phase: Cue slowly bending elbows to bring chest toward wall... [pause 1s]
- Press Phase: Cue pressing back to starting position... [pause 1s]

[Rep 2 — Regular Tempo]
- Descent Phase: Cue slowly bending elbows to bring chest toward wall... [pause 1s]
- Press Phase: Cue pressing back to starting position... [pause 1s]

[Rep 3 — Regular Tempo]
- Descent Phase: Cue slowly bending elbows to bring chest toward wall... [pause 1s]
- Press Phase: Cue pressing back to starting position... [pause 1s]

[Rep 4 — Regular Tempo]
- Descent Phase: Cue slowly bending elbows to bring chest toward wall... [pause 1s]
- Press Phase: Cue pressing back to starting position... [pause 1s]

[Rep 5 — 2-Second Pause]
- Descent Phase: Cue bending elbows toward wall... Hold close... Count: "One... Two..."
- Press Phase: Cue pressing back up... [pause 1s]

[Rep 6 — 2-Second Pause]
- Motivation Cue: Add a quick word of encouragement prior to this final rep.
- Descent Phase: Cue bending elbows toward wall... Hold close... Count: "One... Two..."
- Press Phase: Cue pressing back up... [pause 1s]'
WHERE id = 27;

-- ID 28: Shoulder Raises
UPDATE workouts
SET name = 'Shoulder Raises',
    description = 'Gentle shoulder movements to reduce tension in the neck and shoulders.',
    instructions = 'The user should stand or sit with a straight back. Arms should be relaxed at the sides. Shoulders are raised toward the ears and then lowered toward the floor. Important that shoulders move straight up and straight down without rolling forward. We do 5 repetitions.',
    guidance = 'Style & Persona Guidelines:
Word Choice: You may vary your phrasing (e.g., "Raise your shoulders", "Shrug up toward your ears", "Release down").

Pacing & Timing Rules:
1. Cadence: Speak slowly with distinct gaps.
2. Structural Pauses: Include specified pauses (`...` or `[pause Xs]`) between phases.

Movement Execution Flow (5 Reps Total):

[Rep 1 — Standard Pace]
- Elevation Phase: Cue raising shoulders up... [pause 1s]
- Depression Phase: Cue lowering shoulders smoothly... [pause 1s]

[Rep 2 — Standard Pace]
- Elevation Phase: Cue raising shoulders up... [pause 1s]
- Depression Phase: Cue lowering shoulders smoothly... [pause 1s]

[Rep 3 — Standard Pace]
- Elevation Phase: Cue raising shoulders up... [pause 1s]
- Depression Phase: Cue lowering shoulders smoothly... [pause 1s]

[Rep 4 — Extra Lift Peak]
- Elevation Phase: Cue raising shoulders up... Hold at top... "Now try lifting a tiny bit higher..." [pause 1.5s]
- Depression Phase: Cue lowering shoulders back down... [pause 1s]

[Rep 5 — Extra Lift Peak]
- Motivation Cue: Offer an encouraging remark before this final rep.
- Elevation Phase: Cue raising shoulders up... Hold at top... "Now try lifting a tiny bit higher..." [pause 1.5s]
- Depression Phase: Cue lowering shoulders back down... [pause 1s]'
WHERE id = 28;

-- ID 29: Hand Opens
UPDATE workouts
SET name = 'Hand Opens',
    description = 'Gentle training for hands and fingers.',
    instructions = 'The user should sit or stand with a straight back and arms relaxed at sides. Gently clench both hands into fists, then open hands by extending fingers as much as comfortable. We do 6 repetitions.',
    guidance = 'Style & Persona Guidelines:
Word Choice: Feel free to adjust wording (e.g., "Gently make a fist", "Spread your fingers wide").

Pacing & Timing Rules:
1. Cadence: Unhurried with calm pauses.
2. Structural Pauses: Honor all time gaps.

Movement Execution Flow (6 Reps Total):

[Rep 1 — Standard Tempo]
- Close Phase: Cue gently clenching hands into fists... [pause 1s]
- Open Phase: Cue opening hands and spreading fingers wide... [pause 1s]

[Rep 2 — Standard Tempo]
- Close Phase: Cue gently clenching hands into fists... [pause 1s]
- Open Phase: Cue opening hands and spreading fingers wide... [pause 1s]

[Rep 3 — Standard Tempo]
- Close Phase: Cue gently clenching hands into fists... [pause 1s]
- Open Phase: Cue opening hands and spreading fingers wide... [pause 1s]

[Rep 4 — Standard Tempo]
- Close Phase: Cue gently clenching hands into fists... [pause 1s]
- Open Phase: Cue opening hands and spreading fingers wide... [pause 1s]

[Rep 5 — Deep Stretch]
- Close Phase: Cue gently clenching fists...
- Open Phase: Cue opening fingers wide... Hold open... "Stretch those fingers just a little extra..." [pause 2s]

[Rep 6 — Deep Stretch]
- Motivation Cue: Deliver a positive motivational word before starting this final rep.
- Close Phase: Cue gently clenching fists...
- Open Phase: Cue opening fingers wide... Hold open... "Stretch those fingers just a little extra..." [pause 2s]'
WHERE id = 29;

-- ID 30: Seated Calf Raises
UPDATE workouts
SET name = 'Seated Calf Raises',
    description = 'A simple seated exercise for calf muscles and ankles.',
    instructions = 'The user should sit firmly on a chair with both feet on the floor and a straight back. Lift heels off floor coming onto toes. Slowly lower heels back down. We do 6 repetitions.',
    guidance = 'Style & Persona Guidelines:
Word Choice: Use variations like "Lift onto your toes", "Rise up", "Lower down with control".

Pacing & Timing Rules:
1. Cadence: Controlled with clear spacing between reps.
2. Isometric Hold: Count "One... Two..." during static holds.

Movement Execution Flow (6 Reps Total):

[Rep 1 — Regular Pace]
- Lift Phase: Cue raising heels up... [pause 1s]
- Lower Phase: Cue lowering heels back down with control... [pause 1s]

[Rep 2 — Regular Pace]
- Lift Phase: Cue raising heels up... [pause 1s]
- Lower Phase: Cue lowering heels back down with control... [pause 1s]

[Rep 3 — Regular Pace]
- Lift Phase: Cue raising heels up... [pause 1s]
- Lower Phase: Cue lowering heels back down with control... [pause 1s]

[Rep 4 — Regular Pace]
- Lift Phase: Cue raising heels up... [pause 1s]
- Lower Phase: Cue lowering heels back down with control... [pause 1s]

[Rep 5 — 2-Second Hold]
- Lift Phase: Cue lifting up onto toes... Hold at top... Count: "One... Two..."
- Lower Phase: Cue controlled descent... [pause 1s]

[Rep 6 — 2-Second Hold]
- Motivation Cue: Give a supportive boost before this final rep.
- Lift Phase: Cue lifting up onto toes... Hold at top... Count: "One... Two..."
- Lower Phase: Cue controlled descent... [pause 1s]'
WHERE id = 30;

-- ID 31: Seated Marching
UPDATE workouts
SET name = 'Seated Marching',
    description = 'Calm seated cardio exercise that activates hips and legs.',
    instructions = 'The user should sit far forward on a sturdy chair with a straight back and both feet on the floor. Alternately lift one knee a few centimeters off the floor and place the foot back down. One right and left lift counts as one repetition. We do 6 repetitions.',
    guidance = 'Style & Persona Guidelines:
Form Reminder: Keep reminding the user to maintain a tall, straight posture.

Pacing & Timing Rules:
1. Cadence: Smooth, unhurried pace.

Movement Execution Flow (6 Reps Total):

[Rep 1 — Regular Tempo]
- Right Leg: Cue lifting right knee... [pause 1s] ...Lower down.
- Left Leg: Cue lifting left knee... [pause 1s] ...Lower down.

[Rep 2 — Regular Tempo]
- Right Leg: Cue lifting right knee... [pause 1s] ...Lower down.
- Left Leg: Cue lifting left knee... [pause 1s] ...Lower down.

[Rep 3 — Regular Tempo]
- Right Leg: Cue lifting right knee... [pause 1s] ...Lower down.
- Left Leg: Cue lifting left knee... [pause 1s] ...Lower down.

[Rep 4 — Regular Tempo]
- Right Leg: Cue lifting right knee... [pause 1s] ...Lower down.
- Left Leg: Cue lifting left knee... [pause 1s] ...Lower down.

[Rep 5 — Short Isometric Hold]
- Right Leg: Cue lifting right knee... "Hold it there briefly..." [pause 1.5s] ...Lower.
- Left Leg: Cue lifting left knee... "Hold briefly..." [pause 1.5s] ...Lower.

[Rep 6 — Short Isometric Hold]
- Motivation Cue: Share an encouraging thought before this final rep.
- Right Leg: Cue lifting right knee... "Hold it there briefly..." [pause 1.5s] ...Lower.
- Left Leg: Cue lifting left knee... "Hold briefly..." [pause 1.5s] ...Lower.'
WHERE id = 31;

-- ID 32: Wall Push-Ups
UPDATE workouts
SET name = 'Wall Push-Ups',
    description = 'Light pressing exercise for chest, shoulders, and arms.',
    instructions = 'The user should stand facing a wall, roughly an arm-length away. Place hands against wall at shoulder height. Bend elbows to bring body toward wall without arching lower back. Press back to straight arms. We do 6 repetitions.',
    guidance = 'Style & Persona Guidelines:
Form Cue: Remind user to maintain a straight line from head to heels.

Pacing & Timing Rules:
1. Cadence: Slow descent, powerful press.

Movement Execution Flow (6 Reps Total):

[Rep 1 — Regular Tempo]
- Descent Phase: Cue slowly bending elbows to bring chest toward wall... [pause 1s]
- Press Phase: Cue pressing back to starting position... [pause 1s]

[Rep 2 — Regular Tempo]
- Descent Phase: Cue slowly bending elbows to bring chest toward wall... [pause 1s]
- Press Phase: Cue pressing back to starting position... [pause 1s]

[Rep 3 — Regular Tempo]
- Descent Phase: Cue slowly bending elbows to bring chest toward wall... [pause 1s]
- Press Phase: Cue pressing back to starting position... [pause 1s]

[Rep 4 — Regular Tempo]
- Descent Phase: Cue slowly bending elbows to bring chest toward wall... [pause 1s]
- Press Phase: Cue pressing back to starting position... [pause 1s]

[Rep 5 — 2-Second Pause]
- Descent Phase: Cue bending elbows toward wall... Hold close... Count: "One... Two..."
- Press Phase: Cue pressing back up... [pause 1s]

[Rep 6 — 2-Second Pause]
- Motivation Cue: Add a quick word of encouragement prior to this final rep.
- Descent Phase: Cue bending elbows toward wall... Hold close... Count: "One... Two..."
- Press Phase: Cue pressing back up... [pause 1s]'
WHERE id = 32;

-- ID 33: Supported Side Steps
UPDATE workouts
SET name = 'Supported Side Steps',
    description = 'Controlled side steps that train balance and legs.',
    instructions = 'The user should stand by a sturdy chair or kitchen counter and hold lightly for support as needed. Take a step to the side and bring the other foot next to it without crossing feet. Step both directions. One step right and one step left counts as one repetition. We do 6 repetitions.',
    guidance = 'Style & Persona Guidelines:
Form Cue: Remind user to look forward and hold support if balance feels uncertain.

Pacing & Timing Rules:
1. Cadence: Controlled, steady lateral movement.

Movement Execution Flow (6 Reps Total):

[Rep 1 — Standard Tempo]
- Step Phase: Cue controlled side steps in both directions... [pause 1s]

[Rep 2 — Standard Tempo]
- Step Phase: Cue controlled side steps in both directions... [pause 1s]

[Rep 3 — Standard Tempo]
- Step Phase: Cue controlled side steps in both directions... [pause 1s]

[Rep 4 — Standard Tempo]
- Step Phase: Cue controlled side steps in both directions... [pause 1s]

[Rep 5 — Pause Feet Together]
- Step Phase: Cue side step... Bring feet together and pause... [pause 1.5s] ...Step back.

[Rep 6 — Pause Feet Together]
- Motivation Cue: Provide an encouraging callout before this final rep set.
- Step Phase: Cue side step... Bring feet together and pause... [pause 1.5s] ...Step back.'
WHERE id = 33;

-- ID 34: Chest Opener
UPDATE workouts
SET name = 'Chest Opener',
    description = 'Mobility for chest and shoulders.',
    instructions = 'The user should sit or stand with a straight back. Arms hanging along sides with palms facing forward. Move arms slightly backward and draw shoulder blades gently together. Relax and return to starting position. We do 6 repetitions.',
    guidance = 'Style & Persona Guidelines:
Form Cue: Keep shoulders down away from ears; movement must remain pain-free.

Pacing & Timing Rules:
1. Cadence: Slow, expansive breathing rhythm.

Movement Execution Flow (6 Reps Total):

[Rep 1 — Regular Tempo]
- Open Phase: Cue opening chest and drawing shoulder blades gently together... [pause 1s]
- Release Phase: Cue releasing back to start... [pause 1s]

[Rep 2 — Regular Tempo]
- Open Phase: Cue opening chest and drawing shoulder blades gently together... [pause 1s]
- Release Phase: Cue releasing back to start... [pause 1s]

[Rep 3 — Regular Tempo]
- Open Phase: Cue opening chest and drawing shoulder blades gently together... [pause 1s]
- Release Phase: Cue releasing back to start... [pause 1s]

[Rep 4 — Regular Tempo]
- Open Phase: Cue opening chest and drawing shoulder blades gently together... [pause 1s]
- Release Phase: Cue releasing back to start... [pause 1s]

[Rep 5 — 2-Second Stretch]
- Open Phase: Cue opening chest... Hold the open stretch... Count: "One... Two..."
- Release Phase: Cue easing back to start... [pause 1s]

[Rep 6 — 2-Second Stretch]
- Motivation Cue: Inspire the user before this final rep.
- Open Phase: Cue opening chest... Hold the open stretch... Count: "One... Two..."
- Release Phase: Cue easing back to start... [pause 1s]'
WHERE id = 34;

-- ID 35: Standing Knee Lifts
UPDATE workouts
SET name = 'Standing Knee Lifts',
    description = 'Standing exercise that activates hips, core, and balance.',
    instructions = 'The user should stand near a sturdy chair or kitchen counter and use support as needed. Alternately lift one knee in front of body to a comfortable height and lower foot back down. One right and left lift counts as one repetition. We do 6 repetitions.',
    guidance = 'Style & Persona Guidelines:
Form Cue: Remind user to keep torso tall and upright throughout.

Pacing & Timing Rules:
1. Cadence: Steady lift and controlled lowering.

Movement Execution Flow (6 Reps Total):

[Rep 1 — Regular Tempo]
- Alternate Lifts: Cue lifting right knee... lower with control... [pause 1s] Cue left knee... lower... [pause 1s]

[Rep 2 — Regular Tempo]
- Alternate Lifts: Cue lifting right knee... lower with control... [pause 1s] Cue left knee... lower... [pause 1s]

[Rep 3 — Regular Tempo]
- Alternate Lifts: Cue lifting right knee... lower with control... [pause 1s] Cue left knee... lower... [pause 1s]

[Rep 4 — Regular Tempo]
- Alternate Lifts: Cue lifting right knee... lower with control... [pause 1s] Cue left knee... lower... [pause 1s]

[Rep 5 — Top Hold]
- Hold Lifts: Cue knee raise... "Hold it up briefly..." [pause 1.5s] ...Lower cleanly. Repeat on opposite side.

[Rep 6 — Top Hold]
- Motivation Cue: Boost the user''s spirit before starting this final rep.
- Hold Lifts: Cue knee raise... "Hold it up briefly..." [pause 1.5s] ...Lower cleanly. Repeat on opposite side.'
WHERE id = 35;

-- ID 36: Chair Stand
UPDATE workouts
SET name = 'Chair Stand',
    description = 'Functional strength exercise for legs and glutes.',
    instructions = 'The user should sit far forward on a sturdy chair with feet hip-width apart on the floor. Rise up to standing, then slowly sit back down. Knees should track in the same direction as toes. Armrests may be used if needed. We do 6 repetitions.',
    guidance = 'Style & Persona Guidelines:
Form Cues: Feet firmly planted; ensure knees do not cave inward.

Pacing & Timing Rules:
1. Cadence: Controlled hinge, powerful rise, slow eccentric drop.

Movement Execution Flow (6 Reps Total):

[Rep 1 — Regular Tempo]
- Stand Phase: Cue leaning slightly forward, driving through feet to stand tall... [pause 1s]
- Sit Phase: Cue slowly lowering back down to chair with control... [pause 1s]

[Rep 2 — Regular Tempo]
- Stand Phase: Cue leaning slightly forward, driving through feet to stand tall... [pause 1s]
- Sit Phase: Cue slowly lowering back down to chair with control... [pause 1s]

[Rep 3 — Regular Tempo]
- Stand Phase: Cue leaning slightly forward, driving through feet to stand tall... [pause 1s]
- Sit Phase: Cue slowly lowering back down to chair with control... [pause 1s]

[Rep 4 — Regular Tempo]
- Stand Phase: Cue leaning slightly forward, driving through feet to stand tall... [pause 1s]
- Sit Phase: Cue slowly lowering back down to chair with control... [pause 1s]

[Rep 5 — 2-Second Standing Hold]
- Stand Phase: Cue rising to full standing position... Hold still... Count: "One... Two..."
- Sit Phase: Cue braking movement on the way down to sit... [pause 1s]

[Rep 6 — 2-Second Standing Hold]
- Motivation Cue: Energize the user before doing this final rep.
- Stand Phase: Cue rising to full standing position... Hold still... Count: "One... Two..."
- Sit Phase: Cue braking movement on the way down to sit... [pause 1s]'
WHERE id = 36;

-- ID 37: Backward Leg Extensions
UPDATE workouts
SET name = 'Backward Leg Extensions',
    description = 'Controlled exercise for glutes and hamstrings.',
    instructions = 'The user should stand near a sturdy chair or kitchen counter and hold lightly for support. Extend one leg straight back without leaning forward or arching lower back excessively. Return foot to start and switch legs. One lift per leg counts as one repetition. We do 6 repetitions.',
    guidance = 'Style & Persona Guidelines:
Form Cue: Remind user to keep hips square and facing forward.

Pacing & Timing Rules:
1. Cadence: Steady reach back, controlled return.

Movement Execution Flow (6 Reps Total):

[Rep 1 — Regular Tempo]
- Movement Phase: Cue extending leg back... [pause 1s] ...Return to start before switching legs.

[Rep 2 — Regular Tempo]
- Movement Phase: Cue extending leg back... [pause 1s] ...Return to start before switching legs.

[Rep 3 — Regular Tempo]
- Movement Phase: Cue extending leg back... [pause 1s] ...Return to start before switching legs.

[Rep 4 — Regular Tempo]
- Movement Phase: Cue extending leg back... [pause 1s] ...Return to start before switching legs.

[Rep 5 — 2-Second Hold]
- Hold Phase: Cue leg back... Hold extension... Count: "One... Two..." ...Return with control. Switch side and repeat.

[Rep 6 — 2-Second Hold]
- Motivation Cue: Praise effort before starting this final rep block.
- Hold Phase: Cue leg back... Hold extension... Count: "One... Two..." ...Return with control. Switch side and repeat.'
WHERE id = 37;

-- ID 38: Hip Hinge
UPDATE workouts
SET name = 'Hip Hinge',
    description = 'Standing mobility exercise for hips and hamstrings.',
    instructions = 'The user should stand with feet hip-width apart and hands on hips. Push hips back with knees slightly soft and hinge torso forward keeping a straight spine. Rise back up by pushing hips forward. We do 6 repetitions.',
    guidance = 'Style & Persona Guidelines:
Form Cue: Maintain a long, neutral spine; drive movement directly from hips.

Pacing & Timing Rules:
1. Cadence: Deliberate hinge down, strong drive up.

Movement Execution Flow (6 Reps Total):

[Rep 1 — Standard Pace]
- Hinge Phase: Cue sending hips back with long spine... [pause 1s]
- Drive Phase: Cue standing back up tall... [pause 1s]

[Rep 2 — Standard Pace]
- Hinge Phase: Cue sending hips back with long spine... [pause 1s]
- Drive Phase: Cue standing back up tall... [pause 1s]

[Rep 3 — Standard Pace]
- Hinge Phase: Cue sending hips back with long spine... [pause 1s]
- Drive Phase: Cue standing back up tall... [pause 1s]

[Rep 4 — Standard Pace]
- Hinge Phase: Cue sending hips back with long spine... [pause 1s]
- Drive Phase: Cue standing back up tall... [pause 1s]

[Rep 5 — 2-Second Bottom Hold]
- Hinge Phase: Cue hinging hips back... Hold bottom position... Count: "One... Two..."
- Drive Phase: Cue pressing hips forward to stand tall... [pause 1s]

[Rep 6 — 2-Second Bottom Hold]
- Motivation Cue: Offer positive reinforcement ahead of this final rep.
- Hinge Phase: Cue hinging hips back... Hold bottom position... Count: "One... Two..."
- Drive Phase: Cue pressing hips forward to stand tall... [pause 1s]'
WHERE id = 38;

-- ID 39: Standing March with Arm Drive
UPDATE workouts
SET name = 'Standing March with Arm Drive',
    description = 'Rhythmic full-body movement that raises heart rate.',
    instructions = 'The user should stand upright with feet hip-width apart. March in place by alternately lifting knees and swinging opposite arms. One lift per leg counts as one repetition. We do 6 repetitions.',
    guidance = 'Style & Persona Guidelines:
Form Cue: Remind user to maintain an upright posture and swing opposite arms naturally.

Pacing & Timing Rules:
1. Cadence: Steady marching beat.

Movement Execution Flow (6 Reps Total):

[Rep 1 — Standard Pace]
- March Phase: Cue marching on place with opposite arm drive... [maintain 110 BPM rhythm]

[Rep 2 — Standard Pace]
- March Phase: Cue marching on place with opposite arm drive... [maintain 110 BPM rhythm]

[Rep 3 — Standard Pace]
- March Phase: Cue marching on place with opposite arm drive... [maintain 110 BPM rhythm]

[Rep 4 — Standard Pace]
- March Phase: Cue marching on place with opposite arm drive... [maintain 110 BPM rhythm]

[Rep 5 — High Knees Option]
- March Phase: Cue driving knees slightly higher if feeling stable and balanced!

[Rep 6 — High Knees Option]
- Motivation Cue: Enthusiastic hype phrase before this final march block!
- March Phase: Cue driving knees slightly higher if feeling stable and balanced!'
WHERE id = 39;

-- ID 40: Deep Squats
UPDATE workouts
SET name = 'Deep Squats',
    description = 'Demanding leg strength exercise with a larger range of motion.',
    instructions = 'The user should stand with feet roughly shoulder-width apart. Bend knees and hips in a controlled manner, sinking as deep as mobility and control allow while keeping heels grounded. Drive back up to standing. We do 6 repetitions.',
    guidance = 'Style & Persona Guidelines:
Form Cues: Knees tracking in line with toes; chest stays open and proud.

Pacing & Timing Rules:
1. Cadence: Smooth lower, strong press.

Movement Execution Flow (6 Reps Total):

[Rep 1 — Regular Pace]
- Squat Phase: Cue lowering down with control... [pause 1s]
- Drive Phase: Cue pressing up to standing position... [pause 1s]

[Rep 2 — Regular Pace]
- Squat Phase: Cue lowering down with control... [pause 1s]
- Drive Phase: Cue pressing up to standing position... [pause 1s]

[Rep 3 — Regular Pace]
- Squat Phase: Cue lowering down with control... [pause 1s]
- Drive Phase: Cue pressing up to standing position... [pause 1s]

[Rep 4 — Regular Pace]
- Squat Phase: Cue lowering down with control... [pause 1s]
- Drive Phase: Cue pressing up to standing position... [pause 1s]

[Rep 5 — 2-Second Bottom Pause]
- Squat Phase: Cue sinking down into squat... Hold bottom... Count: "One... Two..."
- Drive Phase: Cue driving back up tall... [pause 1s]

[Rep 6 — 2-Second Bottom Pause]
- Motivation Cue: Fire up the user before this final rep.
- Squat Phase: Cue sinking down into squat... Hold bottom... Count: "One... Two..."
- Drive Phase: Cue driving back up tall... [pause 1s]'
WHERE id = 40;

-- ID 41: Reverse Lunges
UPDATE workouts
SET name = 'Reverse Lunges',
    description = 'Leg strength and balance training through a deep range of motion.',
    instructions = 'The user should stand with feet hip-width apart. Take a long step backward with one foot and lower both knees under control. Drive back up to standing using the front leg and switch sides. One lunge per side counts as one repetition. We do 6 repetitions.',
    guidance = 'Style & Persona Guidelines:
Form Cues: Keep front heel grounded; front knee tracks over toes.

Pacing & Timing Rules:
1. Cadence: Step back, sink, press up, return.

Movement Execution Flow (6 Reps Total):

[Rep 1 — Standard Tempo]
- Lunge Phase: Cue stepping back, controlled dip... [pause 1s] ...Drive up to standing.

[Rep 2 — Standard Tempo]
- Lunge Phase: Cue stepping back, controlled dip... [pause 1s] ...Drive up to standing.

[Rep 3 — Standard Tempo]
- Lunge Phase: Cue stepping back, controlled dip... [pause 1s] ...Drive up to standing.

[Rep 4 — Standard Tempo]
- Lunge Phase: Cue stepping back, controlled dip... [pause 1s] ...Drive up to standing.

[Rep 5 — 2-Second Isometric Hold]
- Lunge Phase: Cue stepping back and sinking low... Hold bottom position... Count: "One... Two..." ...Press back up tall.

[Rep 6 — 2-Second Isometric Hold]
- Motivation Cue: Offer strong encouragement before this final rep sequence.
- Lunge Phase: Cue stepping back and sinking low... Hold bottom position... Count: "One... Two..." ...Press back up tall.'
WHERE id = 41;

-- ID 42: Plank with Shoulder Taps
UPDATE workouts
SET name = 'Plank with Shoulder Taps',
    description = 'Stability exercise for core, shoulders, and coordination.',
    instructions = 'The user should assume a plank position with hands on the floor, arms extended, and body forming a straight line. Lift one hand and touch the opposite shoulder. Place hand down and repeat on the other side. One tap per side counts as one repetition. We do 6 repetitions.',
    guidance = 'Style & Persona Guidelines:
Form Cue: Engage core; keep back flat and limit hip sway.

Pacing & Timing Rules:
1. Cadence: Controlled release and tap.

Movement Execution Flow (6 Reps Total):

[Rep 1 — Regular Tempo]
- Tap Phase: Cue raising right hand to left shoulder... return... [pause 1s] Cue left hand to right shoulder... return... [pause 1s]

[Rep 2 — Regular Tempo]
- Tap Phase: Cue raising right hand to left shoulder... return... [pause 1s] Cue left hand to right shoulder... return... [pause 1s]

[Rep 3 — Regular Tempo]
- Tap Phase: Cue raising right hand to left shoulder... return... [pause 1s] Cue left hand to right shoulder... return... [pause 1s]

[Rep 4 — Regular Tempo]
- Tap Phase: Cue raising right hand to left shoulder... return... [pause 1s] Cue left hand to right shoulder... return... [pause 1s]

[Rep 5 — 1-Second Hold]
- Hold Tap Phase: Cue tapping hand to shoulder... "Hold it there..." [pause 1s] ...Lower back down. Repeat on opposite side.

[Rep 6 — 1-Second Hold]
- Motivation Cue: Drive home a word of praise before this final rep set.
- Hold Tap Phase: Cue tapping hand to shoulder... "Hold it there..." [pause 1s] ...Lower back down. Repeat on opposite side.'
WHERE id = 42;

-- ID 43: Mountain Climbers
UPDATE workouts
SET name = 'Mountain Climbers',
    description = 'Intense cardio exercise with rapid leg alternation.',
    instructions = 'The user should assume a plank position with hands on the floor. Alternately drive one knee toward the chest and return to plank position. One knee drive per leg counts as one repetition. We do 6 repetitions.',
    guidance = 'Style & Persona Guidelines:
Form Cues: Hands under shoulders; keep back level.

Pacing & Timing Rules:
1. Cadence: Fast, brisk cadence while maintaining control.

Movement Execution Flow (6 Reps Total):

[Rep 1 — Brisk Steady Pace]
- Drive Phase: Cue driving knees smoothly toward chest in alternate fashion...

[Rep 2 — Brisk Steady Pace]
- Drive Phase: Cue driving knees smoothly toward chest in alternate fashion...

[Rep 3 — Brisk Steady Pace]
- Drive Phase: Cue driving knees smoothly toward chest in alternate fashion...

[Rep 4 — Brisk Steady Pace]
- Drive Phase: Cue driving knees smoothly toward chest in alternate fashion...

[Rep 5 — Accelerated Tempo]
- Acceleration Phase: Cue picking up the pace slightly faster while maintaining clean form!

[Rep 6 — Accelerated Tempo]
- Motivation Cue: Shout out an encouraging push prior to these final reps.
- Acceleration Phase: Cue picking up the pace slightly faster while maintaining clean form!'
WHERE id = 43;

-- ID 44: Pistol Squat to Chair
UPDATE workouts
SET name = 'Pistol Squat to Chair',
    description = 'Advanced single-leg strength requiring mobility, balance, and control.',
    instructions = 'The user should stand in front of a sturdy chair. Lift one foot off the floor and hold it extended in front of body. Slowly bend standing leg and lower toward chair until glutes lightly touch the seat. Drive back up on same leg. Switch legs after each rep. We do 4 repetitions.',
    guidance = 'Style & Persona Guidelines:
Form Cues: Find single-leg balance first; knee tracks over toes with total control.

Pacing & Timing Rules:
1. Cadence: Slow descent, hold hover, powerful drive up.

Movement Execution Flow (4 Reps Total):

[Rep 1 — Standard Tempo]
- Single-Leg Hinge: Cue balancing on one leg, slowly lowering toward chair... press up... [pause 1s]

[Rep 2 — Standard Tempo]
- Single-Leg Hinge: Cue balancing on one leg, slowly lowering toward chair... press up... [pause 1s]

[Rep 3 — 2-Second Hover Hold]
- Hover Phase: Cue lowering until hovering just above chair seat... Hold hover... Count: "One... Two..." ...Press up strong!

[Rep 4 — 2-Second Hover Hold]
- Motivation Cue: Deliver an inspiring note right before starting this final rep.
- Hover Phase: Cue lowering until hovering just above chair seat... Hold hover... Count: "One... Two..." ...Press up strong!'
WHERE id = 44;

-- ID 45: Burpees
UPDATE workouts
SET guidance = '### Style & Persona Guidelines
1. **Word Choice:** You are FREE to vary your phrasing (e.g., instead of "Hands down", you can say "Plant your hands", "Hands to the floor", or "Drop down"). Adapt wording to match your coaching persona naturally.
2. **Clarity First:** Keep commands concise and clear so the user understands the movement instantly while moving.

### Pacing & Timing Rules (NON-NEGOTIABLE)
1. **Cadence:** Maintain a controlled, rhythmic delivery (~110 BPM).
2. **Structural Pauses:** Regardless of the exact words you choose, you MUST include the required pauses (indicated by `[pause Xs]` or `...`) between movements.
3. **Counting:** When leading isometric holds, count out loud explicitly ("One... Two...") to pace the user.

---

### Movement Execution Flow (6 Reps Total)

#### [Rep 1 — Standard Tempo]
1. **Grounding Phase:** Cue hands to floor... `[pause 1s]`
2. **Extension Phase:** Cue stepping/jumping back to plank... hold briefly to stabilize...
3. **Push Phase:** Cue the push-up... `[pause 1s]`
4. **Return Phase:** Cue bringing feet back forward... `[pause 1s]`
5. **Stand & Finish:** Cue standing up and explosive final jump!
* *Rest Pause:* Stop speaking for 2 seconds before the next rep.

#### [Rep 2 — Standard Tempo]
1. **Grounding Phase:** Cue hands to floor... `[pause 1s]`
2. **Extension Phase:** Cue stepping/jumping back to plank... hold briefly to stabilize...
3. **Push Phase:** Cue the push-up... `[pause 1s]`
4. **Return Phase:** Cue bringing feet back forward... `[pause 1s]`
5. **Stand & Finish:** Cue standing up and explosive final jump!
* *Rest Pause:* Stop speaking for 2 seconds before the next rep.

#### [Rep 3 — Standard Tempo]
1. **Grounding Phase:** Cue hands to floor... `[pause 1s]`
2. **Extension Phase:** Cue stepping/jumping back to plank... hold briefly to stabilize...
3. **Push Phase:** Cue the push-up... `[pause 1s]`
4. **Return Phase:** Cue bringing feet back forward... `[pause 1s]`
5. **Stand & Finish:** Cue standing up and explosive final jump!
* *Rest Pause:* Stop speaking for 2 seconds before the next rep.

#### [Rep 4 — Standard Tempo]
1. **Grounding Phase:** Cue hands to floor... `[pause 1s]`
2. **Extension Phase:** Cue stepping/jumping back to plank... hold briefly to stabilize...
3. **Push Phase:** Cue the push-up... `[pause 1s]`
4. **Return Phase:** Cue bringing feet back forward... `[pause 1s]`
5. **Stand & Finish:** Cue standing up and explosive final jump!
* *Rest Pause:* Stop speaking for 2 seconds before the next rep.

#### [Rep 5 — Isometric Hold]
1. **Grounding Phase:** Cue hands to floor... `[pause 1s]`
2. **Extension Phase:** Cue stepping/jumping back to plank...
3. **Hold Phase:** Direct the user to hold plank for 2 seconds. Count out "One... Two..." out loud.
4. **Push Phase:** Cue the push-up... `[pause 1s]`
5. **Return Phase:** Cue bringing feet back forward... `[pause 1s]`
6. **Stand & Finish:** Cue standing up and explosive final jump!
* *Rest Pause:* Stop speaking for 2 seconds before the next rep.

#### [Rep 6 — Peak Effort]
* **Motivation Cue:** Deliver a personalized, encouraging motivational callout for the final rep.
1. **Grounding Phase:** Cue hands to floor... `[pause 1s]`
2. **Extension Phase:** Cue stepping/jumping back to plank...
3. **Hold Phase:** Direct the user to hold plank for 2 seconds. Count out "One... Two..." out loud.
4. **Push Phase:** Cue the push-up... `[pause 1s]`
5. **Return Phase:** Cue bringing feet back forward... `[pause 1s]`
6. **Stand & Finish:** Cue standing up and explosive final jump!'
WHERE id = 45;

-- ID 46: Bodyweight Single-Leg Deadlift
UPDATE workouts
SET name = 'Bodyweight Single-Leg Deadlift',
    description = 'Advanced single-leg exercise for balance, hips, and hamstrings.',
    instructions = 'The user should stand on one leg with a soft knee bend. Hinge torso forward at hips while extending non-working leg straight back. Keep back flat, then return to standing. Switch legs each rep. We do 6 repetitions.',
    guidance = 'Style & Persona Guidelines:
Form Cues: Find balance first; hinge forward at hips while extending rear leg back; keep hips square.

Pacing & Timing Rules:
1. Cadence: Unhurried hinge and return.

Movement Execution Flow (6 Reps Total):

[Rep 1 — Regular Pace]
- Hinge Phase: Cue hinging forward at hips while reaching free leg back... [pause 1s] ...Return to standing.

[Rep 2 — Regular Pace]
- Hinge Phase: Cue hinging forward at hips while reaching free leg back... [pause 1s] ...Return to standing.

[Rep 3 — Regular Pace]
- Hinge Phase: Cue hinging forward at hips while reaching free leg back... [pause 1s] ...Return to standing.

[Rep 4 — Regular Pace]
- Hinge Phase: Cue hinging forward at hips while reaching free leg back... [pause 1s] ...Return to standing.

[Rep 5 — 2-Second Bottom Hold]
- Hold Hinge: Cue hinging forward... Hold flat-back hinged position... Count: "One... Two..." ...Return tall.

[Rep 6 — 2-Second Bottom Hold]
- Motivation Cue: Send a positive cue before performing this final rep.
- Hold Hinge: Cue hinging forward... Hold flat-back hinged position... Count: "One... Two..." ...Return tall.'
WHERE id = 46;

-- ID 47: Hollow Hold with Knee Tucks
UPDATE workouts
SET name = 'Hollow Hold with Knee Tucks',
    description = 'Advanced core exercise requiring strong abdominals and body control.',
    instructions = 'The user should lie on back with arms extended overhead and legs straight. Engage core to lift shoulder blades and feet off floor. Alternately tuck one knee toward chest and extend back out without losing core tension. One knee tuck per side counts as one rep. We do 6 repetitions.',
    guidance = 'Style & Persona Guidelines:
Form Cue: Keep abdomen tight; press lower back flat against floor throughout.

Pacing & Timing Rules:
1. Cadence: Controlled tuck and extension.

Movement Execution Flow (6 Reps Total):

[Rep 1 — Regular Tempo]
- Tuck Phase: From stable hollow hold, cue tucking knee in... extend back out... [pause 1s] ...Switch legs.

[Rep 2 — Regular Tempo]
- Tuck Phase: From stable hollow hold, cue tucking knee in... extend back out... [pause 1s] ...Switch legs.

[Rep 3 — Regular Tempo]
- Tuck Phase: From stable hollow hold, cue tucking knee in... extend back out... [pause 1s] ...Switch legs.

[Rep 4 — Regular Tempo]
- Tuck Phase: From stable hollow hold, cue tucking knee in... extend back out... [pause 1s] ...Switch legs.

[Rep 5 — Extension Pause]
- Extension Hold: Cue tucking knee, then extending leg straight out... "Hold extended leg briefly..." [pause 1.5s] ...Switch sides.

[Rep 6 — Extension Pause]
- Motivation Cue: Give an energetic boost before this final rep!
- Extension Hold: Cue tucking knee, then extending leg straight out... "Hold extended leg briefly..." [pause 1.5s] ...Switch sides.'
WHERE id = 47;

COMMIT;