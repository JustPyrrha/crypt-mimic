# Crypt: Mimic
[![Made for ModFest: Carnival](https://raw.githubusercontent.com/ModFest/art/v2/badge/svg/carnival/cozy.svg)](https://modfest.net/carnival)
[<img alt="Human Made Mark" src="https://humanmademark.com/white-logo.png" width="196"/>](https://humanmademark.com/)

Not what I set out to make, but still something cool :3

### Usage guide
#### Spawning
`/crypt-mimic <identifier> <x> <y> <z> <nbt>`\
The npc will act as an armour stand until it is locked with:\
`/data modify entity <seletor> Locked set value True`\
While it is unlocked interacting will modify items, once locked dialog actions will show.\
You can also modify model part rotations just as you would an armour stand.
<details>
<summary>NBT tags</summary>

```
NpcId - The npc types id
ArmourItems - A list of item stack compounds
HandItems - A list of item stack compounds
Small - Boolean for small model
Locked - Boolean for locked model
Pose - Compound of pose rotations.
Pose.Head - A list of floats, euler for Head rotation.
Pose.Body - A list of floats, euler for Body rotation.
Pose.LeftArm - A list of floats, euler for Left Arm rotation.
Pose.RightArm - A list of floats, euler for Right Arm rotation.
Pose.LeftLeg - A list of floats, euler for Left Leg rotation.
Pose.RightLeg - A list of floats, euler for Right Leg rotation.
```
</details>

#### NPCs
`data/<namespace>/crypt-mimic/npc/<id>.json`
```json5
// note: this example uses json5 for the comments, use regular json for actual data
{
  // `name` uses the vanilla Text codec, so you can build fancy things
  "name": [ // required
    {
      "type": "translatable",
      "translate": "npc.<namespace>.<id>.name"
    }
  ],
  // `title` also uses the vanilla Text codec
  "title": [ // optional
    {
      "type": "translatable",
      "translate": "npc.<namespace>.<id>.title"
    }
  ],
  "skin": { // optional
    "texture": "<namespace>:textures/entity/npc/<id>.png", // required
    "hasSlimArms": true // optional, defaults to false
  },
  "action": { // optional
    "action": "crypt-mimic:show_dialog", // required
    "value": "<namespace>:dialog_<id>" // may be required, dependent on `action`
  }
}
```
#### Dialog
`data/<namespace>/crypt-mimic/dialog/<id>.json`
```json5
// note: this example uses json5 for the comments, use regular json for actual data
{
  // `text` uses the vanilla Text codec
  "text": [ // required
    {
      "type": "translatable",
      "translate": "dialog.<namespace>.<id>"
    }
  ],
  "actions": [ // required, even if empty but should contain at least one
    {
      "action": "crypt_mimic:show_dialog", // required
      "value": "<namespace>:<different_id>" // may be required, dependent on `action`
    }
  ]
}
```
#### Custom dialog actions
Dialog actions use the `action.<namespace>.<id>` translation key.
```java
import gay.pyrrha.mimic.dialog.DialogAction;
DialogAction.getEVENT().register((player, entity, action) -> {
    // do things
});
```
<details>
<summary>Kotlin Example</summary>

```kotlin
import gay.pyrrha.mimic.dialog.DialogAction
DialogAction.EVENT.register { player, entity, action ->
    // do things (but in kotlin :3)
}
```

</details>

