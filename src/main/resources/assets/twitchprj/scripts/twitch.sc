// [Start] Define Configs

__config() -> {
    'stay_loaded' -> true
};

// [End] Define Configs



// [Start] Define Global Variables

global_enemies = ['zombie', 'skeleton', 'witch'];
global_breaking_sounds = filter(sound(), _~'block\..+\.break');
global_effects = [
    'minecraft:speed','minecraft:slowness','minecraft:haste','minecraft:mining_fatigue','minecraft:strength','minecraft:instant_health','minecraft:instant_damage','minecraft:jump_boost','minecraft:nausea','minecraft:regeneration','minecraft:resistance','minecraft:fire_resistance','minecraft:water_breathing','minecraft:invisibility','minecraft:blindness','minecraft:night_vision','minecraft:hunger','minecraft:weakness','minecraft:poison','minecraft:wither','minecraft:health_boost','minecraft:absorption','minecraft:saturation','minecraft:glowing','minecraft:levitation','minecraft:luck','minecraft:unluck','minecraft:slow_falling','minecraft:conduit_power','minecraft:dolphins_grace','minecraft:bad_omen','minecraft:hero_of_the_village'
];

global_player_pos = [];

// [End] Define Global Variables



// [Start] Twitch Events

__on_twitch_follow(player, actor) -> (
    spawn('armor_stand', pos(player), str('{CustomNameVisible:1b, CustomName: \'{"text": "%s"}\'}', actor));
);

__on_twitch_subscription(player, actor, message, tier, months, resubbed, streak, gifted, gifter) -> (
    if(gifted || tier == 0 || tier == 1,
        (
            spawn('creeper', pos(player));
        ),
        tier == 2,
        (
            modify(player, 'tag', 'gn.chain_break');
        ),
        tier == 3,
        (
            pos = find_spawn_spot(player, 10, 5);
            spawn('ravager', pos, '{Passengers:[{id:"minecraft:vindicator", Tags:["gn.potato_villager"], ActiveEffects:[{Id:14b,Amplifier:1b,Duration:1000000,ShowParticles:0b}], Passengers:[{id:"minecraft:armor_stand", ArmorItems:[{}, {}, {}, {id:"twitchprj:potato_villager", Count:1b}], Small:1b, Invisible:1b, Marker:1b}]}]}');
            sound('event.raid.horn', pos(player), 100, 1, 'master');
        )
    );
);

__on_twitch_bits(player, actor, message, amount) -> (
    if(amount >= 20 && amount < 50,
        (
            modify(player, 'tag', 'gn.lava_block');
        ),
        amount >= 50 && amount < 100,
        (
            spawn('tnt', pos(player), '{Fuse: 80s}');
        ),
        amount >= 100 && amount < 500,
        (
            modify(player, 'effect', 'blindness', 600);
            loop(5,
                pos = find_spawn_spot(player, 5, 2);
                spawn(str('%s', global_enemies:rand(length(global_enemies))), pos, '{Tags: ["gn.enemy"]}');
            );
        ),
        amount >= 500 && amount < 1000,
        (
            pos = find_spawn_spot(player, 5, 2);
            pos:1 = pos:1 + 50;
            spawn('armor_stand', pos, '{Small:1b, Invisible:1b, Tags:["gn.loot_box"], ArmorItems:[{}, {}, {}, {id:"twitchprj:airdrop", Count:1b}]}');
        ),
        amount >= 1000 && amount < 3000,
        (
            modify(player, 'effect', 'slow_falling', 10, 1, false, false, false);
            volume(pos(player) - [5, 6, 5], pos(player) + [5, -10, 5],
                set(_, 'air');
            );
            volume(pos(player) - [5, 5, 5], pos(player) + [5, 5, 5],
                pos = pos(_);
                if(!air(_) && !liquid(_),
                    spawn('falling_block', [round(pos:0) + 0.5, pos:1 + 0.5, round(pos:2) + 0.5], str('{BlockState:{Name: "%s"}, Time:1}', _));
                    set(pos, 'air');
                );
            );
        ),
        amount >= 3000,
        (
            loop(3,
                pos = find_spawn_spot(player, 5, 2);
                spawn('creeper', pos, '{powered:1b}');
            );
        )
    );
);


__on_streamlabs_donation(player, actor, message, amount, formattedAmount, currency) -> (
    if(amount >= 1 && amount < 3,
        (
            modify(player, 'effect', 'speed', 600, 32, false, false, false);
        ),
        amount >= 3 && amount < 5,
        (
            pos = find_spawn_spot(player, 5, 2);
            spawn('illusioner', pos);
        ),
        amount >= 5 && amount < 10,
        (
            loop(5,
                pos = find_spawn_spot(player, 5, 2);
                spawn('wither_skeleton', pos);
            );
        ),
        amount >= 10 && amount < 30,
        (
            modify(player, 'tag', 'gn.earthquake');
            schedule(100, _(outer(player)) -> modify(player, 'clear_tag', 'gn.earthquake'));
            drill_crevasse(pos(player));
        ),
        amount >= 30,
        (
            // Warden
            loop(5,
                pos = find_spawn_spot(player, 5, 2);
                spawn('wither_skeleton', pos);
            );
        )
    );
);

__on_twitch_custom_reward(player, actor, message, badges, subscriptionMonths, customRewardId) -> (
    if(customRewardId == 250,
        (
            effect = global_effects:rand(length(global_effects));
            modify(player, 'effect', effect, 200);
        ),
        customRewardId == 500,
        (
            loop(5,
                pos = find_spawn_spot(player, 5, 2);
                spawn('llama', pos, '{Tags:["gn.angry_llama"]}');
            );
        ),
        customRewardId == 1000,
        (
            modify(player, 'tag', 'gn.apply_shader');
            global_prev_pos = pos(player);
            schedule(1200, _(outer(player)) -> (
                    apply_shader(player, null);
                    modify(player, 'clear_tag', 'gn.apply_shader');
                    run('carpet invertControlsDirection 0');
                );
            );
        ),
        customRewardId == 3000,
        (
            phantom = spawn('phantom', pos(player) + [0, query(player, 'eye_height') + 1, 0], '{NoAI:1b, Tags:["gn.tamed_phantom"]}');
            modify(player, 'mount', phantom);
        ),
        customRewardId == 5000,
        (
            loop(10,
                schedule(_ * 20, _(outer(player)) -> (
                        spawn('falling_block', pos(player) + [0, 40, 0], '{BlockState:{Name:"minecraft:pointed_dripstone",Properties:{thickness:"tip",vertical_direction:"down"}},Silent:1b,Time:1,DropItem:0b,HurtEntities:1b,FallHurtMax:10,FallDistance:2f,FallHurtAmount:2f}');
                        loop(50,
                            pos = find_spawn_spot(player, 10, 0) + [0, 40, 0];
                            spawn('falling_block', pos, '{BlockState:{Name:"minecraft:pointed_dripstone",Properties:{thickness:"tip",vertical_direction:"down"}},Silent:1b,Time:1,DropItem:0b,HurtEntities:1b,FallHurtMax:6,FallDistance:2f,FallHurtAmount:2f}');
                        );
                    );
                );
            );
        ),
        customRewardId == 25000,
        (
            run(str('player %s spawn in survival', actor));
        ),
        customRewardId == 50000,
        (
            loop(50,
                pos = find_spawn_spot(player, 10, 10);
                spawn('chicken', pos, '{Passengers:[{id:"minecraft:zombie",IsBaby:1b,HandItems:[{id:"minecraft:iron_sword",Count:1b},{}]}]}');
            );
        )
    );
);

// [End] Twitch Events



// [Start] Scarpet Events

__on_tick() -> (
    for(entity_selector('@e[type=minecraft:armor_stand, tag=gn.loot_box]'),
        in_dimension(query(_, 'dimension'),
            if(query(_, 'on_ground'),
                (
                    schedule(10, _(outer(_)) -> (
                            modify(_, 'remove');
                            if(bool(rand(2)),
                                (
                                    set(pos(_), 'barrel[facing=up]{LootTable:"twitchprj:barrel/airdrop", CustomName:\'{"text":"Airdrop"}\'}');
                                ),
                                set(pos(_), 'barrel[facing=up]{LootTable:"minecraft:empty", CustomName:\'{"text":"gn.explode"}\'}');
                            );
                        )
                    );
                ),
                (
                    particle('smoke', pos(_) + [0, 1, 0], 20);
                    particle('campfire_cosy_smoke', pos(_) + [0, 1, 0], 3);
                    modify(_, 'effect', 'slow_falling', 10, 1, false, false, false);
                )
            );
        );
    );
    for(player('all'),
        in_dimension(query(_, 'dimension'),
            if(query(_, 'has_tag', 'gn.earthquake'),
                modify(_, 'effect', 'resistance', 1, 255, false, false, false);
                modify(_, 'effect', 'instant_damage', 1, 253, false, false, false);
            );
            if(query(_, 'is_riding') && query(query(_, 'mount'), 'has_tag', 'gn.tamed_phantom'),
                (
                    phantom = query(_, 'mount');
                    modify(phantom, 'accelerate', query(_, 'look') * 0.1);
                    modify(phantom, 'pitch', -1 * query(_, 'pitch'));
                    modify(phantom, 'yaw', query(_, 'yaw'));
                )
            );
            if(query(_, 'has_tag', 'gn.apply_shader'),
                apply_shader(_, null);
                run('carpet invertControlsDirection 2');
            );
            if(query(_, 'player_type') != 'fake',
                (
                    global_player_pos = pos(_) + [0, query(_, 'eye_height'), 0];
                ),
                (
                    run(str('player %s look at %f %f %f', _, global_player_pos:0, global_player_pos:1, global_player_pos:2));
                    run(str('player %s move forward', _));
                    inventory_set(_, query(_, 'selected_slot'), 1, 'egg');
                    if(tick_time() % 5 == 0, run(str('player %s use', _)));
                    if(query(_, 'motion'):0 < 1 || query(_, 'motion'):2 < 1,
                        (
                            run(str('player %s jump', _));
                        ),
                        (
                            run(str('player %s stop', _));
                        )
                    );
                    for(neighbours(pos(_) - [0, 0.5, 0]),
                        if(_ == 'water' || _ == 'lava',
                            block = _;
                            pos = pos(_);
                            set(_, 'blue_ice');
                            schedule(60, _(outer(pos), outer(block)) -> set(pos, block));
                        );
                    );
                    block = query(_, 'trace', 2, 'blocks');
                    if(block && solid(pos(block)),
                        destroy(pos(block) - [0, 1, 0]);
                        destroy(pos(block));
                        destroy(pos(block) + [0, 1, 0]);
                    );
                )
            );
        );
    );
    for(entity_selector('@e[type=vindicator, tag=gn.potato_villager]'),
        if(!query(_, 'is_riding'),
            modify(query(_, 'passengers'):0, 'remove');
            modify(_, 'remove');
        );
        if(query(_, 'is_ridden'),
            armor_stand = query(_, 'passengers'):0;
            pos = global_player_pos;
            look_vec = pos - pos(_) + [0, 0, 0];
            modify(armor_stand, 'look', look_vec);
        );
    );
    for(entity_selector('@e[type=llama, tag=gn.angry_llama]'),
        in_dimension(query(_, 'dimension'),
            angry_lama_ai(_, player('all'):0)
        );
    );
);

__on_player_breaks_block(player, block) -> (
    if(query(player, 'has_tag', 'gn.chain_break'),
        (
            modify(player, 'clear_tag', 'gn.chain_break');
            i = 0;
            for(diamond(pos(block), 2, 2),
                i = i + 1;
                schedule(i, _(outer(_)) -> destroy(_));
            );
        ),
        query(player, 'has_tag', 'gn.lava_block'),
        (
            modify(player, 'clear_tag', 'gn.lava_block');
            set(pos(block), 'lava');
        )
    );
);

__on_player_interacts_with_block(player, hand, block, face, hitvec) -> (
    if(block == 'barrel' && block_data(pos(block)):'CustomName' == '{"text":"gn.explode"}',
        set(pos(block), 'air');
        create_explosion(pos(block));
    );
);

__on_player_interacts_with_entity(player, entity, hand) -> (
    if(!query(player, 'is_riding'),
        if(!query(entity, 'is_ridden') && query(entity, 'has_tag', 'gn.tamed_phantom'),
            modify(player, 'mount', entity);
        );
    );
);

__on_player_connects(player) -> (
    clear_effects(player);
);

__on_start() -> (
    for(player('all'),
        clear_effects(_);
    );
);

// [End] Scarpet Events



// [Start] Custom Function

clear_effects(player) -> (
    if(query(player, 'tags'),
        for(parse_nbt(query(player, 'nbt'):'Tags'),
            modify(player, 'clear_tag', _);
        );
    );
    run('carpet invertControlsDirection 0');
    apply_shader(player, null);
);

generate_random_pos(player, range, min_range) -> (
    pos = pos(player);
    randomized_coords = [if(!bool(rand(2)), round(rand(range)), -1 * round(rand(range))), 0, if(bool(rand(2)), round(rand(range)), -1 * round(rand(range)))];
    spawn_pos = [round(pos:0) + if(bool(rand(2)), min_range, -1 * min_range) + randomized_coords:0 + 0.5, pos:1, round(pos:2) + if(bool(rand(2)), min_range, -1 * min_range) + randomized_coords:2 + 0.5];
);

find_spawn_spot(player, range, min_range) -> (

    spawn_pos = generate_random_pos(player, range, min_range);
    spawn_pos_up = spawn_pos + [0, 1, 0];

    if(solid(spawn_pos) || solid(spawn_pos_up),
        (
            spawn_pos = generate_random_pos(player, range, min_range);
            spawn_pos_up = spawn_pos + [0, 1, 0];
        ),
        spawn_pos;
    );
);

chunk_coords_from_pos(pos) -> [floor(pos:0/16), floor(pos:2/16)];

move_entities(position, range, intensity, radial) -> (
    for(filter(entity_area('*',position, 8, 8, 8), _~'gamemode_id' != 3 ),
        [dx,dy,dz] = pos(_)-position;
        distsq = dx^2+dz^2;
        modify(_,'accelerate', (-radial*dz - (0.5+0.5*intensity)*(dx))/(distsq+1), intensity/sqrt(distsq+1), (radial*dx - (0.5+0.5*intensity)*(dz))/(distsq+1));
    );
);

get_world_seed(pos) -> (
   [chx, chz] = chunk_coords_from_pos(pos);
   system_info('world_seed')+1304011369*chx+2782775717*chz;
);

assert_area_generated(center, r) -> (
    for(range(-r,r+1),
        cx = _;
        for(range(-r,r+1),
            cz = _;
            air(center+16*[cx, 0, dz])
        );
    );
);

drill_crevasse(center) -> (
   assert_area_generated(center, 1);
   seed = get_world_seed(center)+69420;
   reset_seed(seed);
   start_angle = 360*rand(1, seed);
   depth = (center:1+if(system_info('game_major_target')<17,20,20+64))/24;
   loop(2,
      ray = center;
      start_angle += 140+rand(80, seed);
      angle = start_angle;
      loop(24,
         carver = (_(outer(_), outer(ray), outer(seed), outer(depth)) -> (
            size = 24-_;
            move_entities(ray, 8, 0.6, 0);
            loop(10, schedule(rand(4), _(outer(ray)) -> sound(rand(global_breaking_sounds), ray, 1.0, 0.5)));
            for(diamond(ray, size/4, depth*size+rand(10, seed)),
                if(!rand(20), particle('block '+_, _, 2, 0.5 ));
                without_updates(set(_, 'air'));
                if(pos(_):1 <= 15 && pos(_):1 != 0,
                    (
                        set(_, 'lava');
                    ),
                    pos(_):1 == 0,
                    (
                        set(_, 'bedrock');
                    )
                );
            );
         ));
         schedule(40+rand(8, seed)+2*_, carver);
         ray = ray + [sin(angle), 0, cos(angle)];
         angle += rand(10, seed)-5;
      );
   );
);

distance(vec1, vec2) -> sqrt(reduce(vec1 - vec2, _a + _*_, 0));

angry_lama_ai(entity, player) -> (
    if(distance(pos(player), pos(entity)) < 15,
        disp_vec = pos(player) - pos(entity);
        modify(entity, 'look', disp_vec);
        mov_vec = disp_vec;
        mov_vec:1 = 0;
        modify(entity, 'accelerate', (mov_vec + [if(bool(rand(2)), rand(5), -1 * rand(5)), 0, if(bool(rand(2)), rand(5), -1 * rand(5))]) * 0.005);
        if(tick_time() % 20 == 0,
            spit = spawn('llama_spit', pos(entity) + [0, query(entity, 'eye_height'), 0], str('{Owner:%s}', query(entity, 'nbt'):'UUID'));
            modify(spit, 'accelerate', query(entity, 'look'));
        );
    );
);

// [End] Custom Function
