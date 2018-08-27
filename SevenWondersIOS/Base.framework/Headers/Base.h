#import <Foundation/Foundation.h>

@class BaseGame, BasePlayer, BaseBoard, BaseResource, BaseCard, BaseBoardNode, BaseCardFeature, BaseProvideResource, BaseWonders, BaseAction, BaseTakeCard, BaseBuildWonder, BaseStdlibArray;

@protocol BaseStdlibIterator;

NS_ASSUME_NONNULL_BEGIN

@interface KotlinBase : NSObject
-(instancetype) init __attribute__((unavailable));
+(instancetype) new __attribute__((unavailable));
+(void)initialize __attribute__((objc_requires_super));
@end;

@interface KotlinBase (KotlinBaseCopying) <NSCopying>
@end;

__attribute__((objc_runtime_name("KotlinMutableSet")))
@interface BaseMutableSet<ObjectType> : NSMutableSet<ObjectType>
@end;

__attribute__((objc_runtime_name("KotlinMutableDictionary")))
@interface BaseMutableDictionary<KeyType, ObjectType> : NSMutableDictionary<KeyType, ObjectType>
@end;

__attribute__((objc_subclassing_restricted))
@interface BaseGame : KotlinBase
-(instancetype)initWithPlayer1:(BasePlayer*)player1 player2:(BasePlayer*)player2 board:(BaseBoard*)board currentPlayer:(int32_t)currentPlayer NS_SWIFT_NAME(init(player1:player2:board:currentPlayer:)) NS_DESIGNATED_INITIALIZER;

-(BasePlayer*)component1 NS_SWIFT_NAME(component1());
-(BasePlayer*)component2 NS_SWIFT_NAME(component2());
-(BaseBoard*)component3 NS_SWIFT_NAME(component3());
-(int32_t)component4 NS_SWIFT_NAME(component4());
-(BaseGame*)doCopyPlayer1:(BasePlayer*)player1 player2:(BasePlayer*)player2 board:(BaseBoard*)board currentPlayer:(int32_t)currentPlayer NS_SWIFT_NAME(doCopy(player1:player2:board:currentPlayer:));
@property (readonly) BasePlayer* player1;
@property (readonly) BasePlayer* player2;
@property (readonly) BaseBoard* board;
@property int32_t currentPlayer;
@end;

__attribute__((objc_subclassing_restricted))
@interface BasePlayer : KotlinBase
-(instancetype)initWithGold:(int32_t)gold NS_SWIFT_NAME(init(gold:)) NS_DESIGNATED_INITIALIZER;

-(BaseResource*)resources NS_SWIFT_NAME(resources());
-(int32_t)component1 NS_SWIFT_NAME(component1());
-(BasePlayer*)doCopyGold:(int32_t)gold NS_SWIFT_NAME(doCopy(gold:));
@property (readonly) NSMutableArray<BaseCard*>* cards;
@property int32_t gold;
@end;

__attribute__((objc_subclassing_restricted))
@interface BaseBoard : KotlinBase
-(instancetype)initWithCards:(NSMutableArray<BaseBoardNode*>*)cards NS_SWIFT_NAME(init(cards:)) NS_DESIGNATED_INITIALIZER;

-(NSMutableArray<BaseBoardNode*>*)component1 NS_SWIFT_NAME(component1());
-(BaseBoard*)doCopyCards:(NSMutableArray<BaseBoardNode*>*)cards NS_SWIFT_NAME(doCopy(cards:));
@property (readonly) NSMutableArray<BaseBoardNode*>* cards;
@end;

__attribute__((objc_subclassing_restricted))
@interface BaseBoardNode : KotlinBase
-(instancetype)initWithInnerCard:(BaseCard*)innerCard descendants:(NSMutableArray<BaseCard*>*)descendants hidden:(BOOL)hidden NS_SWIFT_NAME(init(innerCard:descendants:hidden:)) NS_DESIGNATED_INITIALIZER;

-(NSMutableArray<BaseCard*>*)component2 NS_SWIFT_NAME(component2());
-(BaseBoardNode*)doCopyInnerCard:(BaseCard*)innerCard descendants:(NSMutableArray<BaseCard*>*)descendants hidden:(BOOL)hidden NS_SWIFT_NAME(doCopy(innerCard:descendants:hidden:));
@property (readonly) BaseCard* _Nullable card;
@property (readonly) NSMutableArray<BaseCard*>* descendants;
@end;

__attribute__((objc_subclassing_restricted))
@interface BaseResource : KotlinBase
-(instancetype)initWithWood:(int32_t)wood clay:(int32_t)clay stone:(int32_t)stone glass:(int32_t)glass papyrus:(int32_t)papyrus gold:(int32_t)gold NS_SWIFT_NAME(init(wood:clay:stone:glass:papyrus:gold:)) NS_DESIGNATED_INITIALIZER;

-(int32_t)component1 NS_SWIFT_NAME(component1());
-(int32_t)component2 NS_SWIFT_NAME(component2());
-(int32_t)component3 NS_SWIFT_NAME(component3());
-(int32_t)component4 NS_SWIFT_NAME(component4());
-(int32_t)component5 NS_SWIFT_NAME(component5());
-(int32_t)component6 NS_SWIFT_NAME(component6());
-(BaseResource*)doCopyWood:(int32_t)wood clay:(int32_t)clay stone:(int32_t)stone glass:(int32_t)glass papyrus:(int32_t)papyrus gold:(int32_t)gold NS_SWIFT_NAME(doCopy(wood:clay:stone:glass:papyrus:gold:));
@property (readonly) int32_t wood;
@property (readonly) int32_t clay;
@property (readonly) int32_t stone;
@property (readonly) int32_t glass;
@property (readonly) int32_t papyrus;
@property (readonly) int32_t gold;
@end;

__attribute__((objc_subclassing_restricted))
@interface BaseCard : KotlinBase
-(instancetype)initWithName:(NSString*)name cost:(BaseResource*)cost features:(NSMutableArray<BaseCardFeature*>*)features NS_SWIFT_NAME(init(name:cost:features:)) NS_DESIGNATED_INITIALIZER;

-(NSString*)component1 NS_SWIFT_NAME(component1());
-(BaseResource*)component2 NS_SWIFT_NAME(component2());
-(NSMutableArray<BaseCardFeature*>*)component3 NS_SWIFT_NAME(component3());
-(BaseCard*)doCopyName:(NSString*)name cost:(BaseResource*)cost features:(NSMutableArray<BaseCardFeature*>*)features NS_SWIFT_NAME(doCopy(name:cost:features:));
@property (readonly) NSString* name;
@property (readonly) BaseResource* cost;
@property (readonly) NSMutableArray<BaseCardFeature*>* features;
@end;

@interface BaseCardFeature : KotlinBase
@end;

__attribute__((objc_subclassing_restricted))
@interface BaseProvideResource : BaseCardFeature
-(instancetype)initWithResource:(BaseResource*)resource NS_SWIFT_NAME(init(resource:)) NS_DESIGNATED_INITIALIZER;

-(BaseResource*)component1 NS_SWIFT_NAME(component1());
-(BaseProvideResource*)doCopyResource:(BaseResource*)resource NS_SWIFT_NAME(doCopy(resource:));
@property (readonly) BaseResource* resource;
@end;

__attribute__((objc_subclassing_restricted))
@interface BaseWonders : KotlinBase
-(instancetype)initWithState:(BaseGame*)state NS_SWIFT_NAME(init(state:)) NS_DESIGNATED_INITIALIZER;

-(void)takeActionAction:(BaseAction*)action NS_SWIFT_NAME(takeAction(action:));
@property (readonly) BaseGame* gameState;
@end;

@interface BaseAction : KotlinBase
@end;

__attribute__((objc_subclassing_restricted))
@interface BaseTakeCard : BaseAction
-(instancetype)initWithCard:(BaseCard*)card NS_SWIFT_NAME(init(card:)) NS_DESIGNATED_INITIALIZER;

-(BaseCard*)component1 NS_SWIFT_NAME(component1());
-(BaseTakeCard*)doCopyCard:(BaseCard*)card NS_SWIFT_NAME(doCopy(card:));
@property (readonly) BaseCard* card;
@end;

__attribute__((objc_subclassing_restricted))
@interface BaseBuildWonder : BaseAction
-(instancetype)initWithWonderNo:(int32_t)wonderNo NS_SWIFT_NAME(init(wonderNo:)) NS_DESIGNATED_INITIALIZER;

-(int32_t)component1 NS_SWIFT_NAME(component1());
-(BaseBuildWonder*)doCopyWonderNo:(int32_t)wonderNo NS_SWIFT_NAME(doCopy(wonderNo:));
@property (readonly) int32_t wonderNo;
@end;

__attribute__((objc_subclassing_restricted))
@interface Base : KotlinBase
+(void)mainArgs:(BaseStdlibArray*)args NS_SWIFT_NAME(main(args:));
@end;

__attribute__((objc_subclassing_restricted))
@interface BaseStdlibArray : KotlinBase
+(instancetype)arrayWithSize:(int32_t)size init:(id _Nullable(^)(NSNumber*))init NS_SWIFT_NAME(init(size:init:));

+(instancetype)alloc __attribute__((unavailable));
+(instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));

-(id _Nullable)getIndex:(int32_t)index NS_SWIFT_NAME(get(index:));
-(id<BaseStdlibIterator>)iterator NS_SWIFT_NAME(iterator());
-(void)setIndex:(int32_t)index value:(id _Nullable)value NS_SWIFT_NAME(set(index:value:));
@property (readonly) int32_t size;
@end;

@protocol BaseStdlibIterator
@required
-(BOOL)hasNext NS_SWIFT_NAME(hasNext());
-(id _Nullable)next NS_SWIFT_NAME(next());
@end;

NS_ASSUME_NONNULL_END
