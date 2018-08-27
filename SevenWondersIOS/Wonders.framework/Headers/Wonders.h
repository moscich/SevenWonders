#import <Foundation/Foundation.h>

@class Game, Player, Board, Resource, Card, BoardNode, CardFeature, ProvideResource, Wonders, Action, TakeCard, BuildWonder, StdlibArray;

@protocol StdlibIterator;

NS_ASSUME_NONNULL_BEGIN

@interface KotlinBase : NSObject
-(instancetype) init __attribute__((unavailable));
+(instancetype) new __attribute__((unavailable));
+(void)initialize __attribute__((objc_requires_super));
@end;

@interface KotlinBase (KotlinBaseCopying) <NSCopying>
@end;

__attribute__((objc_runtime_name("KotlinMutableSet")))
@interface MutableSet<ObjectType> : NSMutableSet<ObjectType>
@end;

__attribute__((objc_runtime_name("KotlinMutableDictionary")))
@interface MutableDictionary<KeyType, ObjectType> : NSMutableDictionary<KeyType, ObjectType>
@end;

__attribute__((objc_subclassing_restricted))
@interface Game : KotlinBase
-(instancetype)initWithPlayer1:(Player*)player1 player2:(Player*)player2 board:(Board*)board currentPlayer:(int32_t)currentPlayer NS_SWIFT_NAME(init(player1:player2:board:currentPlayer:)) NS_DESIGNATED_INITIALIZER;

-(Player*)component1 NS_SWIFT_NAME(component1());
-(Player*)component2 NS_SWIFT_NAME(component2());
-(Board*)component3 NS_SWIFT_NAME(component3());
-(int32_t)component4 NS_SWIFT_NAME(component4());
-(Game*)doCopyPlayer1:(Player*)player1 player2:(Player*)player2 board:(Board*)board currentPlayer:(int32_t)currentPlayer NS_SWIFT_NAME(doCopy(player1:player2:board:currentPlayer:));
@property (readonly) Player* player1;
@property (readonly) Player* player2;
@property (readonly) Board* board;
@property int32_t currentPlayer;
@end;

__attribute__((objc_subclassing_restricted))
@interface Player : KotlinBase
-(instancetype)initWithGold:(int32_t)gold NS_SWIFT_NAME(init(gold:)) NS_DESIGNATED_INITIALIZER;

-(Resource*)resources NS_SWIFT_NAME(resources());
-(int32_t)component1 NS_SWIFT_NAME(component1());
-(Player*)doCopyGold:(int32_t)gold NS_SWIFT_NAME(doCopy(gold:));
@property (readonly) NSMutableArray<Card*>* cards;
@property int32_t gold;
@end;

__attribute__((objc_subclassing_restricted))
@interface Board : KotlinBase
-(instancetype)initWithCards:(NSMutableArray<BoardNode*>*)cards NS_SWIFT_NAME(init(cards:)) NS_DESIGNATED_INITIALIZER;

-(NSMutableArray<BoardNode*>*)component1 NS_SWIFT_NAME(component1());
-(Board*)doCopyCards:(NSMutableArray<BoardNode*>*)cards NS_SWIFT_NAME(doCopy(cards:));
@property (readonly) NSMutableArray<BoardNode*>* cards;
@end;

__attribute__((objc_subclassing_restricted))
@interface BoardNode : KotlinBase
-(instancetype)initWithInnerCard:(Card*)innerCard descendants:(NSMutableArray<Card*>*)descendants hidden:(BOOL)hidden NS_SWIFT_NAME(init(innerCard:descendants:hidden:)) NS_DESIGNATED_INITIALIZER;

-(NSMutableArray<Card*>*)component2 NS_SWIFT_NAME(component2());
-(BoardNode*)doCopyInnerCard:(Card*)innerCard descendants:(NSMutableArray<Card*>*)descendants hidden:(BOOL)hidden NS_SWIFT_NAME(doCopy(innerCard:descendants:hidden:));
@property (readonly) Card* _Nullable card;
@property (readonly) NSMutableArray<Card*>* descendants;
@end;

__attribute__((objc_subclassing_restricted))
@interface Resource : KotlinBase
-(instancetype)initWithWood:(int32_t)wood clay:(int32_t)clay stone:(int32_t)stone glass:(int32_t)glass papyrus:(int32_t)papyrus gold:(int32_t)gold NS_SWIFT_NAME(init(wood:clay:stone:glass:papyrus:gold:)) NS_DESIGNATED_INITIALIZER;

-(int32_t)component1 NS_SWIFT_NAME(component1());
-(int32_t)component2 NS_SWIFT_NAME(component2());
-(int32_t)component3 NS_SWIFT_NAME(component3());
-(int32_t)component4 NS_SWIFT_NAME(component4());
-(int32_t)component5 NS_SWIFT_NAME(component5());
-(int32_t)component6 NS_SWIFT_NAME(component6());
-(Resource*)doCopyWood:(int32_t)wood clay:(int32_t)clay stone:(int32_t)stone glass:(int32_t)glass papyrus:(int32_t)papyrus gold:(int32_t)gold NS_SWIFT_NAME(doCopy(wood:clay:stone:glass:papyrus:gold:));
@property (readonly) int32_t wood;
@property (readonly) int32_t clay;
@property (readonly) int32_t stone;
@property (readonly) int32_t glass;
@property (readonly) int32_t papyrus;
@property (readonly) int32_t gold;
@end;

__attribute__((objc_subclassing_restricted))
@interface Card : KotlinBase
-(instancetype)initWithName:(NSString*)name cost:(Resource*)cost features:(NSMutableArray<CardFeature*>*)features NS_SWIFT_NAME(init(name:cost:features:)) NS_DESIGNATED_INITIALIZER;

-(NSString*)component1 NS_SWIFT_NAME(component1());
-(Resource*)component2 NS_SWIFT_NAME(component2());
-(NSMutableArray<CardFeature*>*)component3 NS_SWIFT_NAME(component3());
-(Card*)doCopyName:(NSString*)name cost:(Resource*)cost features:(NSMutableArray<CardFeature*>*)features NS_SWIFT_NAME(doCopy(name:cost:features:));
@property (readonly) NSString* name;
@property (readonly) Resource* cost;
@property (readonly) NSMutableArray<CardFeature*>* features;
@end;

@interface CardFeature : KotlinBase
@end;

__attribute__((objc_subclassing_restricted))
@interface ProvideResource : CardFeature
-(instancetype)initWithResource:(Resource*)resource NS_SWIFT_NAME(init(resource:)) NS_DESIGNATED_INITIALIZER;

-(Resource*)component1 NS_SWIFT_NAME(component1());
-(ProvideResource*)doCopyResource:(Resource*)resource NS_SWIFT_NAME(doCopy(resource:));
@property (readonly) Resource* resource;
@end;

__attribute__((objc_subclassing_restricted))
@interface Wonders : KotlinBase
-(instancetype)initWithState:(Game*)state NS_SWIFT_NAME(init(state:)) NS_DESIGNATED_INITIALIZER;

-(void)takeActionAction:(Action*)action NS_SWIFT_NAME(takeAction(action:));
@property (readonly) Game* gameState;
@end;

@interface Action : KotlinBase
@end;

__attribute__((objc_subclassing_restricted))
@interface TakeCard : Action
-(instancetype)initWithCard:(Card*)card NS_SWIFT_NAME(init(card:)) NS_DESIGNATED_INITIALIZER;

-(Card*)component1 NS_SWIFT_NAME(component1());
-(TakeCard*)doCopyCard:(Card*)card NS_SWIFT_NAME(doCopy(card:));
@property (readonly) Card* card;
@end;

__attribute__((objc_subclassing_restricted))
@interface BuildWonder : Action
-(instancetype)initWithWonderNo:(int32_t)wonderNo NS_SWIFT_NAME(init(wonderNo:)) NS_DESIGNATED_INITIALIZER;

-(int32_t)component1 NS_SWIFT_NAME(component1());
-(BuildWonder*)doCopyWonderNo:(int32_t)wonderNo NS_SWIFT_NAME(doCopy(wonderNo:));
@property (readonly) int32_t wonderNo;
@end;

__attribute__((objc_subclassing_restricted))
@interface  : KotlinBase
+(void)mainArgs:(StdlibArray*)args NS_SWIFT_NAME(main(args:));
@end;

__attribute__((objc_subclassing_restricted))
@interface StdlibArray : KotlinBase
+(instancetype)arrayWithSize:(int32_t)size init:(id _Nullable(^)(NSNumber*))init NS_SWIFT_NAME(init(size:init:));

+(instancetype)alloc __attribute__((unavailable));
+(instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));

-(id _Nullable)getIndex:(int32_t)index NS_SWIFT_NAME(get(index:));
-(id<StdlibIterator>)iterator NS_SWIFT_NAME(iterator());
-(void)setIndex:(int32_t)index value:(id _Nullable)value NS_SWIFT_NAME(set(index:value:));
@property (readonly) int32_t size;
@end;

@protocol StdlibIterator
@required
-(BOOL)hasNext NS_SWIFT_NAME(hasNext());
-(id _Nullable)next NS_SWIFT_NAME(next());
@end;

NS_ASSUME_NONNULL_END
